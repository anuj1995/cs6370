
#from django.http import Http404
from django.http import HttpResponse
from django.shortcuts import render
from .models import Artist, Artwork, Customer, Orders
from django.conf import settings
from django.core.files.storage import FileSystemStorage



# Create your views here.
def index(request):
    context = {

    }
    return render(request, 'artists/index.html', context)


def artist_register(request):
    context = {

    }
    return render(request, 'artists/artist_register.html', context)


def customer_register(request):
    context = {

    }
    return render(request, 'artists/customer_register.html', context)


def gallery(request):
    all_artwork = Artwork.objects.all()
    context = {
        'all_artwork' : all_artwork
    }
    return render(request, 'artists/gallery.html', context)


def add_artwork(request):
    if request.method == 'POST':
        if request.POST.get('art_title') and request.POST.get('price'):
            artist = Artist.objects.get(pk=1)
            art = Artwork()
            #Get file to be converted
            #myfile = request.FILES['myfile']
            #fs = FileSystemStorage()
            #filename = fs.save(myfile.name, myfile)

            #art.image = filename

            myfile = request.FILES['myfile']
            fs = FileSystemStorage()
            filename = fs.save(myfile.name, myfile)

            art.image = filename
            art.art_title = request.POST['art_title']
            art.artist_id = artist
            art.price = request.POST['price']
            art.isAvailable = True
            art.save()
            return render(request, 'artists/add_artwork.html')
    else:
            return render(request, 'artists/add_artwork.html')
