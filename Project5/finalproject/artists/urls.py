from django.urls import path
from . import views

app_name = 'artists'
urlpatterns = [
    path('', views.index, name='index'),
    path('customer_register/', views.customer_register, name='customer_register'),
    path('artist_register/', views.artist_register, name='artist_register'),
    path('gallery/', views.gallery, name='gallery'),
    path('add_artwork/', views.add_artwork, name='add_artwork'),

    #path('customer-register/', views.customer_register, name='customer_register'),
    #path('artist-register/', views.artist_register, name='artist_register'),
    #path('<int:pk>/', views.DetailView.as_view(), name='detail'),
    #path('<int:pk>/results/', views.ResultsView.as_view(), name='results'),
    #path('<int:art_id>/add/', views.add, name='add'),
]
