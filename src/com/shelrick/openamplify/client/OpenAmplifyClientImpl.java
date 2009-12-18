package com.shelrick.openamplify.client;
/*
 Copyright (c) 2009 Gregory J. Gershman

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
*/
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class OpenAmplifyClientImpl implements OpenAmplifyClient
{
	private final HttpClient httpClient = new HttpClient();

	private String apiUrl = "http://portaltnx.openamplify.com/AmplifyWeb_v11/AmplifyThis?";
	private String apiKey = null;
	
	public OpenAmplifyClientImpl(String apiKey)
	{
		this.apiKey = apiKey;
	}
	
	public void setApiUrl(String apiUrl)
	{
		this.apiUrl = apiUrl;
	}
	
	public String getApiUrl()
	{
		return this.apiUrl;
	}
	
	public void setApiKey(String apiKey)
	{
		this.apiKey = apiKey;
	}
	
	public String getApiKey()
	{
		return this.apiKey;
	}
	
	@Override
	public OpenAmplifyResponse analyzeText(String inputText, Analysis analysis, Integer maxTopicResults) throws OpenAmplifyClientException
	{
		List<NameValuePair> params = getBaseParams();
		inputText = inputText.replaceAll("'@[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]@'", " ");
		params.add(new NameValuePair("inputtext", inputText));
		params.add(new NameValuePair("analysis", analysis.type()));
		params.add(new NameValuePair("maxtopicresult", maxTopicResults.toString()));
		
		PostMethod postMethod = new PostMethod(apiUrl);
		postMethod.addParameters(params.toArray(new NameValuePair[1]));
					
		String responseText = doRequest(postMethod);
		//System.out.println(responseText);
		return parseResponse(responseText, analysis);
	}

	@Override
	public OpenAmplifyResponse analyzeUrl(String url, Analysis analysis, Integer maxTopicResults) throws OpenAmplifyClientException
	{
		List<NameValuePair> params = getBaseParams();
		params.add(new NameValuePair("sourceurl", url));
		params.add(new NameValuePair("analysis", analysis.type()));
		params.add(new NameValuePair("maxtopicresult", maxTopicResults.toString()));
		
		GetMethod getMethod = new GetMethod(apiUrl);
		getMethod.setQueryString(params.toArray(new NameValuePair[1]));
		
		String responseText = doRequest(getMethod);	
		return parseResponse(responseText, analysis);
	}

	@Override
	public OpenAmplifyResponse searchText(String inputText, Analysis analysis, List<String> searchTermsList, Integer maxTopicResults) throws OpenAmplifyClientException
	{
		List<NameValuePair> params = getBaseParams();
		params.add(new NameValuePair("inputtext", inputText));
		params.add(new NameValuePair("analysis", analysis.type()));
		params.add(new NameValuePair("searchTerms", getSearchTerms(searchTermsList)));
		params.add(new NameValuePair("maxtopicresult", maxTopicResults.toString()));
		
		PostMethod postMethod = new PostMethod(apiUrl);
		postMethod.addParameters(params.toArray(new NameValuePair[1]));
		
		String responseText = doRequest(postMethod);			
		return parseResponse(responseText, analysis);
	}
	
	@Override
	public OpenAmplifyResponse searchUrl(String url, Analysis analysis, List<String> searchTermsList, Integer maxTopicResults) throws OpenAmplifyClientException
	{
		List<NameValuePair> params = getBaseParams();
		params.add(new NameValuePair("sourceurl", url));
		params.add(new NameValuePair("analysis", analysis.type()));
		params.add(new NameValuePair("searchTerms", getSearchTerms(searchTermsList)));
		params.add(new NameValuePair("maxtopicresult", maxTopicResults.toString()));
		
		GetMethod getMethod = new GetMethod(apiUrl);
		getMethod.setQueryString(params.toArray(new NameValuePair[1]));
		
		String responseText = doRequest(getMethod);
		return parseResponse(responseText, analysis);
	}
	
	private List<NameValuePair> getBaseParams()
	{
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new NameValuePair("apikey", apiKey));
		params.add(new NameValuePair("outputformat", "xml"));
		//params.add(new NameValuePair("optimiseRespTime", "disable"));
		return params;
	}
	
	private String getSearchTerms(List<String> searchTermsList)
	{
		StringBuffer searchTerms = new StringBuffer();
		if (!searchTermsList.isEmpty())
		{
			for (String searchTerm: searchTermsList)
			{
				searchTerm = searchTerm.trim();
				if (searchTerm.indexOf(" ") > 0)
				{
					searchTerm = "\"" + searchTerm + "\"";
				}
				searchTerms.append(searchTerm);
				searchTerms.append(" ");
			}
		}		
		return searchTerms.toString().trim();
	}
	
	private String doRequest(HttpMethod method) throws OpenAmplifyClientException
	{
		try
		{
			httpClient.executeMethod(method);
			if (method.getStatusCode() != 200)
			{
				throw new OpenAmplifyClientException("Error: code=" + method.getStatusCode() + " with message: " + method.getStatusText());
			}
			else
			{
				return method.getResponseBodyAsString();
			}
		}
		catch (HttpException httpe)
		{
			throw new OpenAmplifyClientException(httpe);
		}
		catch (IOException ioe)
		{
			throw new OpenAmplifyClientException(ioe);
		}
	}
	
	private OpenAmplifyResponse parseResponse(String responseText, Analysis analysis) throws OpenAmplifyClientException
	{
		OpenAmplifyResponse response = new OpenAmplifyResponse();
		SAXBuilder saxBuilder = new SAXBuilder();
		try
		{
			Document document = saxBuilder.build(new StringReader(responseText));
			Element rootElement = document.getRootElement();
			Element returnElement = parseReturnElement(rootElement, analysis);
			
			if (analysis == Analysis.ALL || analysis == Analysis.TOPICS)
			{
				response.setTopics(parseTopics(returnElement));
			}
			/*
			 * TODO: implement parsing for the other response elements
			if (analysis == Analysis.ALL || analysis == Analysis.ACTIONS)
			{
				response.setActions(parseActions(returnElement));
			}
			if (analysis == Analysis.ALL || analysis == Analysis.DEMOGRAPHICS)
			{
				response.setDemographics(parseDemographics(returnElement));
			}
			if (analysis == Analysis.ALL || analysis == Analysis.STYLES)
			{
				response.setStyles(parseStyles(returnElement));
			}
			*/
		}
		catch (JDOMException je)
		{
			throw new OpenAmplifyClientException(je);
		}
		catch (IOException ioe)
		{
			throw new OpenAmplifyClientException(ioe);
		}
		return response;
	}
	
	private Element parseReturnElement(Element rootElement, Analysis analysis) throws OpenAmplifyClientException
	{
		if (analysis == Analysis.ALL)
		{
			return rootElement.getChild("AmplifyReturn");
		}
		else if (analysis == Analysis.TOPICS)
		{
			return rootElement.getChild("TopicsReturn");
		}
		else
		{
			throw new OpenAmplifyClientException("Analysis type " + analysis.name() + " response parsing not yet implemented.");
		}
	}
	
	private Topics parseTopics(Element returnElement)
	{
		Element topicsElement = returnElement.getChild("Topics");
		
		Element domainsElement = topicsElement.getChild("Domains");
		Element topTopicsElement = topicsElement.getChild("TopTopics");
		Element properNounsElement = topicsElement.getChild("ProperNouns");
		Element locationsElement = topicsElement.getChild("Locations");
	
		Element domainResultElement = domainsElement.getChild("DomainResult");
		Domains domains = new Domains();
		if (domainResultElement != null)
		{
			Element domainElement = domainResultElement.getChild("Domain");
			Domain domain = new Domain();
			domain.setName(domainElement.getChild("Name").getText());
			domain.setValue(Float.parseFloat(domainElement.getChild("Value").getText()));
			domains.setDomain(domain);
			
			Element subdomainsElement = domainResultElement.getChild("Subdomains");
			List subdomainResultElements = subdomainsElement.getChildren("SubdomainsResult");
			List<Domain> subdomains = new ArrayList<Domain>();
			for (int i = 0; i < subdomainResultElements.size(); i++)
			{
				Element subdomainResultElement = (Element)subdomainResultElements.get(i);
				Element subdomainElement = subdomainResultElement.getChild("Subdomain");
				Element scoresElement = subdomainResultElement.getChild("Scores");
				List<Topic> scores = parseTopicResults(scoresElement.getChildren("TopicResult"));
				Domain subdomain = new Domain();
				subdomain.setName(subdomainElement.getChildText("Name"));
				subdomain.setValue(Float.parseFloat(subdomainElement.getChildText("Value")));
				subdomain.setScores(scores);
				subdomains.add(subdomain);
			}
			domains.setSubDomains(subdomains);
		}
		List topTopicTopicResultElements = topTopicsElement.getChildren("TopicResult");
		List<Topic> topTopics = parseTopicResults(topTopicTopicResultElements);
		
		List properNounTopicResultElements = (List<Element>)properNounsElement.getChildren("TopicResult");
		List<Topic> properNouns = parseTopicResults(properNounTopicResultElements);
		
		List locationResultsElement = locationsElement.getChildren("Result");
		List<Location> locations = parseLocationResults(locationResultsElement);
		
		Topics topics = new Topics();
		topics.setDomains(domains);
		topics.setTopTopics(topTopics);
		topics.setProperNouns(properNouns);
		topics.setLocations(locations);
		
		return topics;
	}
	
	private List<Topic> parseTopicResults(List topicResultElements)
	{
		List<Topic> topics = new ArrayList<Topic>();
		for (int i = 0; i < topicResultElements.size(); i++)
		{
			Element topicResultElement = (Element)topicResultElements.get(i);
			Element topicElement = topicResultElement.getChild("Topic");
			
			Topic topic = new Topic();
			Element nameElement = topicElement.getChild("Name");
			topic.setName(nameElement.getText());
			Element valueElement = topicElement.getChild("Value");
			topic.setValue(Float.parseFloat(valueElement.getText()));
			
			Element polarityElement = topicResultElement.getChild("Polarity");
			Element minElement = polarityElement.getChild("Min");
			Element minNameElement = minElement.getChild("Name");
			Element minValueElement = minElement.getChild("Value");
			topic.setMinPolarityName(minNameElement.getText());
			topic.setMinPolarityValue(Float.parseFloat(minValueElement.getText()));
			
			Element meanElement = polarityElement.getChild("Mean");
			Element meanNameElement = meanElement.getChild("Name");
			Element meanValueElement = meanElement.getChild("Value");
			topic.setMeanPolarityName(meanNameElement.getText());
			topic.setMeanPolarityValue(Float.parseFloat(meanValueElement.getText()));
			
			Element maxElement = polarityElement.getChild("Min");
			Element maxNameElement = maxElement.getChild("Name");
			Element maxValueElement = maxElement.getChild("Value");
			topic.setMaxPolarityName(maxNameElement.getText());
			topic.setMaxPolarityValue(Float.parseFloat(maxValueElement.getText()));
			
			Element offeringGuidanceElement = topicResultElement.getChild("OfferingGuidance");
			Element offeringGuidanceNameElement = offeringGuidanceElement.getChild("Name");
			Element offeringGuidanceValueElement = offeringGuidanceElement.getChild("Value");
			topic.setOfferingGuidanceName(offeringGuidanceNameElement.getText());
			topic.setOfferingGuidanceValue(Float.parseFloat(offeringGuidanceValueElement.getText()));
			
			Element requestingGuidanceElement = topicResultElement.getChild("RequestingGuidance");
			Element requestingGuidanceNameElement = requestingGuidanceElement.getChild("Name");
			Element requestingGuidanceValueElement = requestingGuidanceElement.getChild("Value");
			topic.setRequestingGuidanceName(requestingGuidanceNameElement.getText());
			topic.setRequestingGuidanceValue(Float.parseFloat(requestingGuidanceValueElement.getText()));
			
			topics.add(topic);
		}
		return topics;
	}
	
	private List<Location> parseLocationResults(List locationResultsElement)
	{
		List<Location> locations = new ArrayList<Location>();
		for (int i = 0; i < locationResultsElement.size(); i++)
		{
			Element resultElement = (Element)locationResultsElement.get(i);
			Element nameElement = resultElement.getChild("Name");
			Element valueElement = resultElement.getChild("Value");
			
			Location location = new Location();
			location.setName(nameElement.getText());
			location.setValue(Float.parseFloat(valueElement.getText()));
			
			locations.add(location);
		}
		return locations;
	}
}
