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

def about(request):
    context = {

    }
    return render(request, 'artists/about.html', context)


def artist_register(request):
    if request.method == 'POST':
        first_name = request.POST['first_name']
        last_name = request.POST['last_name']
        username = request.POST['username']
        email = request.POST['email']
        password = request.POST['password']

        user = User.objects.create_user(username=username, password=password,email=email, first_name=first_name, last_name=last_name)
        user.save()
        return render(request, 'artists/artist_register.html')
    else:
        return render(request, 'artists/artist_register.html')

    
def customer_register(request):
    if request.method == 'POST':
        first_name = request.POST['first_name']
        last_name = request.POST['last_name']
        username = request.POST['username']
        email = request.POST['email']
        password = request.POST['password']

        user = User.objects.create_user(username=username, password=password,email=email, first_name=first_name, last_name=last_name)
        user.save()
        return render(request, 'artists/customer_register.html')
    else:
        return render(request, 'artists/customer_register.html')


def gallery(request):
    all_artwork = Artwork.objects.all()
    context = {
        'all_artwork' : all_artwork
    }
    return render(request, 'artists/gallery.html', context)


def add_artwork(request):
    if request.method == 'POST':
        if request.POST.get('art_title') and request.POST.get('price'):
            if User.is_authenticated:
                artist = Artist.objects.get(username = request.user.username)

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

def customer_login(request):
    if request.method == 'POST':
        username = request.POST['customer_username']
        password = request.POST['customer_password']

        user = auth.authenticate(username=username, password=password)

        if user is not None:
            auth.login(request, user)
            return render(request, 'artists/customer_login.html')
    else:
        return render(request, 'artists/customer_login.html')


def artist_login(request):
    if request.method == 'POST':
        username = request.POST['artist_username']
        password = request.POST['artist_password']

        user = auth.authenticate(username=username, password=password)

        if user is not None:
            auth.login(request, user)
            return render(request, 'artists/artist_login.html')
    else:
        return render(request, 'artists/artist_login.html') 
    
def order(request):
    if request.method == 'POST':
        art_id = request.POST['art_id']

        artwork = Artwork.objects.get(id = art_id)
        artwork.isAvailable = False
        artwork.save()

    context = {
        'artwork' : artwork
    }
    return render(request, 'artists/order.html', context)

    
def artist_display(request):
    all_artwork = Artwork.objects.all()
    context = {
        'all_artwork' : all_artwork
    }
    return render(request, 'artists/artist_display.html', context)
