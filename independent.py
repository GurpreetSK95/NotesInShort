
import json
import os
import sys
import requests
sys.path.append('/home/satwik/Desktop/angelhack/comprehensive_search')

from comprehensive_search import comprehensive_search
ENDPOINTS_PROJECT_DIR = os.path.join(os.path.dirname(__file__),
                                      'havenondemand-python')
sys.path.append(ENDPOINTS_PROJECT_DIR)
import havenondemand
from havenondemand.hodclient import *
client = HODClient("36a143cf-7451-4246-b245-0ac8ddd104ab", version="v1")

import ssl
from functools import wraps
def sslwrap(func):
    @wraps(func)
    def bar(*args, **kw):
        kw['ssl_version'] = ssl.PROTOCOL_TLSv1
        return func(*args, **kw)
    return bar

ssl.wrap_socket = sslwrap(ssl.wrap_socket)
print 'imports passed'
sentiment_url = 'https://api.havenondemand.com/1/api/sync/analyzesentiment/v1?text='
ocr_url = 'https://api.havenondemand.com/1/api/sync/ocrdocument/v1?file=@sample_image.png'
def requestCompleted(response, error, **kwargs):
  print response

mock_text = ''
with open('mock_text.txt', 'r') as fp:
	mock_text = fp.read().replace('\n', '')



def ocr(path="/home/satwik/Desktop/angelhack/sample_image.png"):
	params = {'file': path}
	#options for mode - document_photo , scene_photo
	response = client.post_request(params, HODApps.OCR_DOCUMENT, async=False)
	if response is None:
		error = client.get_last_error();
		for err in error.errors:
			print ("Error code: %d \nReason: %s \nDetails: %s\n" % (err.error,err.reason, err.detail))
	print "Ocr done"
	return response['text_block'][0]['text']

def sentiment_analysis(text):
	response = client.get_request({'text': text}, HODApps.ANALYZE_SENTIMENT, async=False)
	if response is None:
		error = client.get_last_error();
		for err in error.errors:
			print ("Error code: %d \nReason: %s \nDetails: %s\n" % (err.error,err.reason, err.detail))
	print 'sentiment analysis done!'
	return response

def entity_extraction(text):
	entities = ['people_eng', 'places_eng', 'companies_eng', 'films_eng', 'organizations_eng', 'professions_eng', 'date_eng','internet_email']
	response = client.get_request({'text': text, 'entity_type':entities}, HODApps.ENTITY_EXTRACTION, async=False)
	if response is None:
		error = client.get_last_error();
		for err in error.errors:
			print ("Error code: %d \nReason: %s \nDetails: %s\n" % (err.error,err.reason, err.detail))
	print 'entity extraction done!'
	return response

def concept_extraction(text):
	concept_url = 'https://api.havenondemand.com/1/api/sync/extractconcepts/v1?text='
	response = requests.post(concept_url+text, {'apikey':'36a143cf-7451-4246-b245-0ac8ddd104ab'})
	#print response.content
	concepts = json.loads(response.content)['concepts']
	return sorted(concepts, key=lambda k:k['occurrences'], reverse=True)

def create_wikipedia_links(entity):
	i = 15
	useful_entities = []
	for entity in top_entities:
		if (i>0):
			if entity['additional_information'] is not 'null':
				try:
					useful_entities.append({'name':entity['original_text'], 'wikipedia_link':entity['additional_information']['wikipedia_eng'], 'image_link':entity['additional_information']['image']})
					i-=1
				except:
					pass
		return useful_entities


def concept_to_keyword(concepts):
	keywords= [w['concept'] for w in concepts[:5]]
	return keywords







#response = client.get_request({'text': 'I love Haven OnDemand!'}, HODApps.ANALYZE_SENTIMENT, async=False)
#print response
#res = requests.post(ocr_url, {'apikey':'36a143cf-7451-4246-b245-0ac8ddd104ab'})
# print res.content

def image_analysis(image_path):

	image_text = ocr()
	#do some sentiment analysis
	text_sentiments = sentiment_analysis(image_text)
	overall_sentiment = text_sentiments["aggregate"]["sentiment"]
	positive_sentiment_score = float(text_sentiments["aggregate"]["score"])
	postive_sentiments = [ w['sentiment'] for w in text_sentiments["positive"]]
	negative_sentiments = [ w['sentiment'] for w in text_sentiments["negative"]]

	#do some entity extraction
	entities = entity_extraction(mock_text)


	top_entities = sorted(entities["entities"], key=lambda k: k["score"])
	#extract those entities who have additional information
	#Extract top 10 wikipedia links in entity_name, wikipedia_link tuple

	useful_entities = create_wikipedia_links(top_entities)

	concepts = concept_extraction(mock_text)


	keywords = concept_to_keyword(concepts)


	#search for relevant images
	related_images_search = comprehensive_search(keywords)

