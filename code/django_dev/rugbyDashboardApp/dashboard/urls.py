from django.urls import path
from . import views

urlpatterns = [
   path('', views.index, name="index"),
   path('login/', views.login_view, name="login"),
   path('logout/', views.logout_view, name="logout"),
   path('matches/', views.prev_matches_view, name="previous_matches"),
   path('team_performance/<str:match_id>', views.team_performance_view, name='team_performance'),
   path('filter_performance/<str:match_id>/', views.filter_performance, name='filter_performance'),
]