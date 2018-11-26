# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey has `on_delete` set to the desired behavior.
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models


class Artist(models.Model):
    a_id = models.CharField(primary_key=True, max_length=50)
    a_name = models.CharField(max_length=50)
    a_email = models.CharField(max_length=50)
    a_password = models.CharField(max_length=256)
    gender = models.CharField(db_column='Gender', max_length=50, blank=True, null=True)  # Field name made lowercase.
    ph_number = models.IntegerField(db_column='Ph_number', blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'artist'


class ArtistSells(models.Model):
    art = models.ForeignKey('Artwork', models.DO_NOTHING)
    price = models.FloatField()
    a = models.ForeignKey(Artist, models.DO_NOTHING)
    date = models.DateField(db_column='Date', blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'artist_sells'


class Artwork(models.Model):
    art_id = models.CharField(primary_key=True, max_length=50)
    price = models.FloatField()
    art_title = models.CharField(max_length=50)
    image = models.TextField()
    available = models.IntegerField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'artwork'


class Customer(models.Model):
    c_id = models.CharField(primary_key=True, max_length=50)
    c_name = models.CharField(max_length=50)
    c_email = models.CharField(max_length=50)
    c_password = models.CharField(db_column='C_password', max_length=256, blank=True, null=True)  # Field name made lowercase.
    gender = models.CharField(db_column='Gender', max_length=50, blank=True, null=True)  # Field name made lowercase.
    ph_number = models.IntegerField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'customer'


class CustomerBuys(models.Model):
    c = models.ForeignKey(Customer, models.DO_NOTHING)
    art = models.ForeignKey(Artwork, models.DO_NOTHING)
    date_of_purchase = models.DateField(db_column='Date_of_purchase', blank=True, null=True)  # Field name made lowercase.

    class Meta:
        managed = False
        db_table = 'customer_buys'


class OrderDetail(models.Model):
    order_id = models.CharField(primary_key=True, max_length=50)
    art_id = models.CharField(max_length=50)
    a_id = models.CharField(max_length=50)
    stock_id = models.CharField(max_length=50, blank=True, null=True)
    subtotal = models.FloatField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'order_detail'


class Orders(models.Model):
    order = models.ForeignKey(OrderDetail, models.DO_NOTHING)
    date_created = models.DateField(db_column='Date_created')  # Field name made lowercase.
    date_shipped = models.DateField()
    c_id = models.CharField(max_length=50)
    art = models.ForeignKey(Artwork, models.DO_NOTHING)
    shipping = models.ForeignKey('ShippingInfo', models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'orders'


class ShippingInfo(models.Model):
    shipping_id = models.CharField(primary_key=True, max_length=50)
    shipping_type = models.CharField(max_length=50)
    date_shipped = models.CharField(max_length=50)
    shipping_cost = models.CharField(max_length=50)
    shipping_address = models.CharField(max_length=50)

    class Meta:
        managed = False
        db_table = 'shipping_info'
