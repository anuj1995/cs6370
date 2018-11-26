from django.contrib import admin
from .models import Artist, ArtistSells, Artwork, Customer
from .models import CustomerBuys, OrderDetail, Orders, ShippingInfo
# Register your models here.
"""
class ChoiceInline(admin.TabularInline):
    model = Choice
    extra = 3

class QuestionAdmin(admin.ModelAdmin):
    fieldsets = [
        (None,               {'fields': ['question_text']}),
        ('Date information', {'fields': ['pub_date'], 'classes': ['collapse']}),
    ]
    inlines = [ChoiceInline]
    list_display = ('question_text', 'pub_date', 'was_published_recently')
    list_filter = ['pub_date']
    search_fields = ['question_text']

admin.site.register(Question, QuestionAdmin)
"""

class ArtworkInline(admin.TabularInline):
    model = Artwork
    extra = 3

class ArtworkAdmin(admin.ModelAdmin):
    fieldsets = [
        (None,               {'fields': ['art_title']}),
        ('Art ID', {'fields': ['art_id'], 'classes': ['collapse']}),
    ]
    inlines = [ArtworkInline]
    list_display = ('art_title', 'art_id', 'a_id', 'is_sold')
    list_filter = ['art_id']
    search_fields = ['art_title']
