import firebase_admin
from firebase_admin import db, credentials, auth
from datetime import datetime, timedelta
from firebase_admin.auth import UserNotFoundError

# Load the service account credentials
cred = credentials.Certificate("dashboard/rugby_credentials.json")

# Initialise Firebase Admin SDK with the credentials and database URL
firebase_admin.initialize_app(cred, {"databaseURL": "https://rugby-stats-40be8-default-rtdb.europe-west1.firebasedatabase.app/"})

# Create reference to root node of the database
ref = db.reference("/")

# Get match data for a specific match_id from the Firebase Realtime Database
def fetch_match_data(match_id):
    new_ref = ref.child(f"matches/{match_id}")
    return new_ref.get()

# Get player performance data for a specific match, with optional filtering by time window and location
def get_players_performance_per_match(match_id, time_window=None, location=None):
    new_ref = ref.child(f"matches/{match_id}")
    specified_match = new_ref.get()
    
    if not specified_match:
        return {} # Return empty dictionary if no corresponding match_id in database

    player_stats = {}

    events = specified_match.get("events", {})

    # Get list of event timestamps to determine match end time (last_timestamp)
    event_timestamps = [event_data["timeStamp"] for event_data in events.values()] 
    if not event_timestamps:
        return {} # Empty dict returned if no events found
    
    last_timestamp = max(event_timestamps) # Calculate the match end time
    last_event_timestamp = datetime.strptime(last_timestamp, "%H:%M:%S") # Preprocessing timestamp string to time object
    
    for event_id, event_data in events.items():
        
        # Skip an event if location is provided but not identical to the event's location
        if location and event_data.get("location") != location:
            continue

        event_timestamp = datetime.strptime(event_data["timeStamp"], "%H:%M:%S")

        # Filter events by time window if provided 
        if time_window:
            if event_timestamp < (last_event_timestamp - timedelta(minutes=time_window)):
                continue # Skip event if outside of time window
        
        player = event_data["playerNumber"]
        event_type = event_data["event"]

        # Initialise player stats if not already present
        if player not in player_stats:
            player_stats[player] = {}
        if event_type not in player_stats[player]:
            player_stats[player][event_type] = 0
        
        # Increment the event count for the player
        player_stats[player][event_type] += 1
    
    return player_stats

# Generates a heatmap based on event locations for a specific match
def get_match_heatmap(match_id):
    new_ref = ref.child(f"matches/{match_id}")
    specified_match = new_ref.get()

     # If no match data found, return an empty dictionary
    if not specified_match:
        return {}
    
    # Initialise heatmap with predefined locations
    heatmap_dict = {"q1": 0, "q2": 0, "q3": 0, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0}
    
    events = specified_match.get("events", {})
    
    # Count the number of events for each location
    for event_id, event_data in events.items():
        location = event_data["location"]
        heatmap_dict[location] += 1
    
    return heatmap_dict

# Generates event trends by date for a collection of matches.
def get_team_event_trends(matches):
   
    trends = {}
    for match_id, match_data in matches.items():
        date_str = match_data.get("date")
        
        if not date_str:
            continue    # Skip if no date available
        
        try:
            date = datetime.strptime(date_str, "%Y/%m/%d").date() # Preprocessing date string to python date object
        except ValueError:
            continue    # Skip matches with invalid date format

        # Initialise trend data for the date if not already present
        if date not in trends:
            trends[date] = {}
        
        events = match_data.get("events", {})

        # Count the occurrences of each event type by date
        for event_id, event_data in events.items():
            event_type = event_data.get("event")
            trends[date][event_type] = trends[date].get(event_type, 0) + 1

    return trends