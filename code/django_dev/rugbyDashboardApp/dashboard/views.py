import json
from django.http import JsonResponse
from django.shortcuts import render, redirect
from .firebase import auth, db
from .firebase import get_players_performance_per_match, get_match_heatmap
from .firebase import get_team_event_trends
from .firebase import fetch_match_data
from firebase_admin.auth import UserNotFoundError, InvalidIdTokenError


# View for landing page (Dashboard)
def index(request):
    user_id = request.session.get("user_id")
    if not user_id:
        return redirect("login")

    try:
        ref = db.reference("/matches")
        # Query for matches where the "userId" field is equal to the current user's ID
        matches = ref.order_by_child("userId").equal_to(user_id).get()
        
        # Get  event trends for the team based on the fetched matches
        team_trends = get_team_event_trends(matches)

        # Converts the dictionary keys to strings ensuring JSON compatibility
        # JSON doesn't support non-string keys like datetime objects
        team_trends_str_keys = {str(k): v for k, v in team_trends.items()}
        
        # Render the dashboard html page, passing the team trends 
        return render(request, "dashboard.html", {
            "team_trends": json.dumps(team_trends_str_keys),
            "active_page": "dashboard"  # sets the active page to "dashboard"
        })
    
    except Exception as e:
        return JsonResponse({"status": "error", "message": str(e)})


# View for User Login
def login_view(request):
    if request.method == "POST":
        
        data = json.loads(request.body)
        id_token = data.get("id_token") # get id_token sent from the front end js
        
        if not id_token:
            return JsonResponse({"status": "error", "message": "The ID token is empty"})
        
        try:
            verified_token = auth.verify_id_token(id_token) # Verifies the ID token with Firebase Admin SDK
            
            uid = verified_token["uid"]  # Gets user's UID from the verified token
            
            request.session["user_id"] = uid # Stores the UID in the session
            return JsonResponse({"status": "success", "user_id": uid})
       
        except InvalidIdTokenError:
            return JsonResponse({"status": "error", "message": "Invalid Token"})
        
        except UserNotFoundError:
            return JsonResponse({"status": "error", "message": "User Not Found"})
        
        except Exception as e:
            return JsonResponse({"status": "error", "message": str(e)})
    
    return render(request, "login.html")


# View for User Logout
def logout_view(request):
    request.session.flush()    # Logs out existing user by clearing their session
    return redirect("login")


# View for displaying list of all the user's matches
def prev_matches_view(request):

    user_id = request.session.get("user_id")
    if not user_id:
        return redirect("login")

    try:
        ref = db.reference("/matches")
        matches = ref.order_by_child("userId").equal_to(user_id).get()
        return render(request, "previous_matches.html", {"matches": matches, "active_page": "matches"})
    
    except Exception as e:
        return JsonResponse({"status": "error", "message": str(e)})


# View for displaying team performance in a match
def team_performance_view(request, match_id):
    user_id = request.session.get("user_id")
    if not user_id:
        return redirect("login")
    try:
        match_details = fetch_match_data(match_id)
        players_performance = get_players_performance_per_match(match_id)
        match_heatmap = get_match_heatmap(match_id)
        return render(request, "team_performance.html", {
            "match_id": match_id,
            "match": match_details,
            "players_performance": json.dumps(players_performance),
            "match_heatmap": json.dumps(match_heatmap)
        })
    
    except Exception as e:
        return JsonResponse({"status": "error", "message": str(e)})


# View for filtering players' performance based on Time window, Player number and Pitch location
def filter_performance(request, match_id):
    # Gets the minutes parameter from the query string, defaulting to "all"
    minutes = request.GET.get("minutes", "all")
    if minutes == "all":
        minutes = None    # Entire match duration since no time filter provided
    else:
        minutes = int(minutes)
    
    # Gets player number parameter from the query string, treating "null" as None
    player_number = request.GET.get("playerNumber", None)
    if player_number == "null":
        player_number = None    # If no player number is provided set to None

    # Gets the location parameter from the query string, where empty string ("") indicates no location filter
    location = request.GET.get("location", "")
    if location == "":
        location = None

    # Get players' performances for the match based on the selected time window
    player_stats = get_players_performance_per_match(match_id, time_window=minutes, location=location)

    filtered_stats = {}
    # Filter the performance data by player number if provided
    for player, events in player_stats.items():
        if player_number is None or player == int(player_number):
            filtered_stats[player] = events
    
    # Define the event types
    event_types = ["Knock-On", "Positive Turnover", "Negative Turnover", "Lineout Win", "Lineout Loss"]

    # Prepare data with the event types and the corresponding counts for each player
    data = {
        player: {event_type: events.get(event_type, 0) for event_type in event_types}
        for player, events in filtered_stats.items()
    }

    return JsonResponse(data)