# Generated by Django 2.1.3 on 2018-11-29 00:42

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('artists', '0003_auto_20181128_1731'),
    ]

    operations = [
        migrations.AlterField(
            model_name='artwork',
            name='image',
            field=models.FileField(upload_to=''),
        ),
    ]
