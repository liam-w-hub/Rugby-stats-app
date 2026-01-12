import json
from django.test import TestCase, Client
from django.urls import reverse
from unittest.mock import patch

# Unit Tests for Django Views
class ViewsUnitTests(TestCase):

    def setUp(self):
        # Creates a test client and sets a default user session
        self.client = Client()
        session = self.client.session
        session["user_id"] = "test_uid"
        session.save()

    def test_index_view_redirects_if_no_session(self):
        # Removes the user session and ensures index redirects to login
        session = self.client.session
        session.pop("user_id", None)
        session.save()

        response = self.client.get(reverse("index"))
        self.assertEqual(response.status_code, 302)
        self.assertIn(reverse("login"), response.url)

    def test_index_view_ok_with_session(self):
        # Confirms that the index view loads dashboard.html when logged in
        response = self.client.get(reverse("index"))
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, "dashboard.html")

    @patch("dashboard.views.auth.verify_id_token")
    def test_login_post_ok(self, mock_verify):
        # Mocks token verification & checks a valid token logs in the user
        mock_verify.return_value = {"uid": "test_uid_login"}
        data = {"id_token": "valid_token"}
        response = self.client.post(
            reverse("login"),
            data=json.dumps(data),
            content_type="application/json"
        )

        self.assertEqual(response.status_code, 200)
        self.assertIn("success", response.content.decode())
        self.assertEqual(self.client.session["user_id"], "test_uid_login")

    def test_logout_view(self):
        # Checks that logout clears the session & redirects to login
        response = self.client.get(reverse("logout"))
        self.assertEqual(response.status_code, 302)
        self.assertIn(reverse("login"), response.url)
        self.assertNotIn("user_id", self.client.session)

    @patch("dashboard.views.db")
    def test_prev_matches_view(self, mock_db):
        # Ensures previous_matches renders for logged-in users
        fake_matches = {"match1": {"userId": "test_uid", "events": {}}}
        mock_db.reference.return_value.order_by_child.return_value.equal_to.return_value.get.return_value = fake_matches

        response = self.client.get(reverse("previous_matches"))
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, "previous_matches.html")

    @patch("dashboard.views.fetch_match_data")
    @patch("dashboard.views.get_players_performance_per_match")
    @patch("dashboard.views.get_match_heatmap")
    def test_team_performance_view(self, mock_heatmap, mock_perf, mock_fetch):
        # Verifies team_performance renders & fetches context data
        mock_fetch.return_value = {"teamName": "Team A", "opponentName": "Team B"}
        mock_perf.return_value = {"7": {"Knock-On": 2}}
        mock_heatmap.return_value = {"q1": 5, "q2": 0, "q3": 2, "q4": 0, "q5": 0, "q6": 0, "q7": 0, "q8": 0}

        response = self.client.get(reverse("team_performance", args=["123"]))
        self.assertEqual(response.status_code, 200)
        self.assertTemplateUsed(response, "team_performance.html")

    @patch("dashboard.views.get_players_performance_per_match")
    def test_filter_performance_view(self, mock_perf):
        # Simulates a GET to filter_performance and checks JSON output
        mock_perf.return_value = {9: {"Knock-On": 1, "Try": 1}}
        url = reverse("filter_performance", args=["123"])
        response = self.client.get(url, {"minutes": "all", "playerNumber": "9", "location": ""})
        self.assertEqual(response.status_code, 200)
        data = json.loads(response.content)
        self.assertIn("9", data)


# Unit Tests for Firebase Helpers
class FirebaseHelperTests(TestCase):

    @patch("dashboard.firebase.ref")
    def test_fetch_match_data(self, mock_ref):
        # Checks fetch_match_data returns correct match data
        mock_ref.child.return_value.get.return_value = {"teamName": "Team A"}
        from dashboard import firebase
        result = firebase.fetch_match_data("123")
        self.assertEqual(result, {"teamName": "Team A"})

    @patch("dashboard.firebase.ref")
    def test_get_match_heatmap(self, mock_ref):
        # Tests a simple heatmap function
        mock_ref.child.return_value.get.return_value = {
            "events": {
                "e1": {"location": "q1"},
                "e2": {"location": "q3"}
            }
        }
        from dashboard import firebase
        heatmap = firebase.get_match_heatmap("123")
        self.assertEqual(heatmap["q1"], 1)
        self.assertEqual(heatmap["q3"], 1)
