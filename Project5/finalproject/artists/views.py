from django.utils import timezone
from django.shortcuts import get_object_or_404, render
from django.http import HttpResponse, HttpResponseRedirect
#from .models import Choice, Question
from .models import Artist, Artwork, Customer, Orders
from django.template import loader
from django.urls import reverse
from django.views import generic
from django.views.generic import ListView

# Create your views here.
def editartwork(request, art_id):
    artwork = get_object_or_404(Artwork, pk=art_id)
    try:
        selected_art = artwork.choice_set.get(pk=request.POST['art'])
    except (KeyError, Artwork.DoesNotExist):
        # Redisplay the question voting form.
        return render(request, 'artists/detail.html', {
            'artwork': artwork,
            'error_message': "You didn't select any artwork.",
        })
    else:
        selected_choice.votes += 1
        selected_choice.save()
        # Always return an HttpResponseRedirect after successfully dealing
        # with POST data. This prevents data from being posted twice if a
        # user hits the Back button.
        return HttpResponseRedirect(reverse('artists:results', args=(artwork.art_id,)))

class IndexView(generic.ListView):
    template_name = 'artists/index.html'
    context_object_name = 'latest_artwork_list'

    def get_queryset(self):
        """
        Return all artwork in the database.
        """
        return Artwork.objects.all()


class DetailView(generic.DetailView):
    model = Artwork
    template_name = 'artists/detail.html'

    def get_queryset(self):
        """
        Return all artwork in the database.
        """
        return Artwork.objects.all()

class ResultsView(generic.DetailView):
    model = Artwork
    template_name = 'artists/results.html'
