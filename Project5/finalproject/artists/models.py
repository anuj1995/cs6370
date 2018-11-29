# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey has `on_delete` set to the desired behavior.
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models

class Artwork(models.Model):
    id = models.AutoField(primary_key=True)
    artist_id = models.ForeignKey('Artist', on_delete=models.CASCADE, db_column='artist_id')
    price = models.DecimalField(max_digits=5, decimal_places=2)
    art_title = models.CharField(max_length=50)
    image = models.FileField()
    isAvailable = models.BooleanField(blank=True)

    def __str__(self):
        return "{} - {} {} - {}".format(self.art_title, self.artist_id.first_name, self.artist_id.last_name,self.price)

    class Meta:
        db_table = 'artwork'


class Artist(models.Model):
    id = models.CharField(primary_key=True, max_length=50)
    #art_id = models.ForeignKey('Artwork', on_delete=models.CASCADE, db_column='art_id')
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
    order_id = models.ForeignKey('Orders', on_delete=models.CASCADE, db_column='order_id')

    class Meta:
        db_table = 'customer'


class Orders(models.Model):
    id = models.CharField(primary_key=True, max_length=50)
    art_id = models.ForeignKey(Artwork, on_delete=models.CASCADE, db_column='art_id')
    artist_id = models.ForeignKey(Artist, on_delete=models.CASCADE, db_column='artist_id')
    customer_id = models.ForeignKey(Customer, on_delete=models.CASCADE, db_column='customer_id')

    class Meta:
        db_table = 'orders'
