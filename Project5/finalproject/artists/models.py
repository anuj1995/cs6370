# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey has `on_delete` set to the desired behavior.
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models

class Artwork(models.Model):
    id = models.CharField(primary_key=True, max_length=50)
    artist_id = models.ForeignKey('Artist', on_delete=models.CASCADE)
    price = models.DecimalField(max_digits=5, decimal_places=2)
    art_title = models.CharField(max_length=50)
    image = models.TextField()
    isAvailable = models.BooleanField(blank=True)

    class Meta:
        db_table = 'artwork'


class Artist(models.Model):
    id = models.CharField(primary_key=True, max_length=50)
    art_id = models.ForeignKey(Artwork, on_delete=models.CASCADE)
    first_name = models.CharField(max_length=50)
    last_name = models.CharField(max_length=50)
    username = models.CharField(max_length=50)
    email = models.CharField(max_length=50)
    password = models.CharField(max_length=256)

    class Meta:
        db_table = 'artist'


class Customer(models.Model):
    id = models.CharField(primary_key=True, max_length=50)
    first_name = models.CharField(max_length=50)
    last_name = models.CharField(max_length=50)
    username = models.CharField(max_length=50)
    email = models.CharField(max_length=50)
    password = models.CharField(max_length=256)  # Field name made lowercase.
    order_id = models.ForeignKey('Orders', on_delete=models.CASCADE)

    class Meta:
        db_table = 'customer'


class Orders(models.Model):
    id = models.CharField(primary_key=True, max_length=50)
    art_id = models.ForeignKey(Artwork, on_delete=models.CASCADE)
    artist_id = models.ForeignKey(Artist, on_delete=models.CASCADE)
    customer_id = models.ForeignKey(Customer, on_delete=models.CASCADE)

    class Meta:
        db_table = 'orders'
