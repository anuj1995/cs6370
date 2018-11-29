from django.urls import path
from . import views

app_name = 'artists'
urlpatterns = [
    path('', views.index, name='index'),
    path('about/', views.about, name='about'),
    path('customer_register/', views.customer_register, name='customer_register'),
    path('artist_register/', views.artist_register, name='artist_register'),
    path('customer_login/', views.customer_login, name='customer_login'),
    path('artist_login/', views.artist_login, name='artist_login'),
    path('gallery/', views.gallery, name='gallery'),
    path('add_artwork/', views.add_artwork, name='add_artwork'),
    path('artist_display/', views.artist_display, name='artist_display'),

]
