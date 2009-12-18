package com.shelrick.openamplify.client.example;
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

import java.util.ArrayList;
import java.util.List;

import com.shelrick.openamplify.client.Analysis;
import com.shelrick.openamplify.client.Location;
import com.shelrick.openamplify.client.OpenAmplifyClient;
import com.shelrick.openamplify.client.OpenAmplifyClientException;
import com.shelrick.openamplify.client.OpenAmplifyClientImpl;
import com.shelrick.openamplify.client.OpenAmplifyResponse;
import com.shelrick.openamplify.client.Topic;

public class OpenAmplifyClientExample
{
	public static void main(String[] args)
	{
		OpenAmplifyClient oaClient = new OpenAmplifyClientImpl("dgs85gpqpgaawu7dz3hc8cn87fz5f8u2");
		
		String input = "I need a new dog.  My old dog was killed by a horrible fox in New Jersey.";
		List<String> searchTermsList = new ArrayList<String>();
		searchTermsList.add("fox");
		String url = "http://baltimorejewish.com";
		
		try
		{
			OpenAmplifyResponse response = oaClient.analyzeText(input, Analysis.ALL, 10);
			for (Topic topic: response.getTopics().getTopTopics())
			{
				System.out.println(topic.getName());
				System.out.println(topic.getRequestingGuidanceName());
			}
			for (Topic topic: response.getTopics().getProperNouns())
			{
				System.out.println(topic.getName());
				System.out.println(topic.getValue());
			}
			for (Location location: response.getTopics().getLocations())
			{
				System.out.println(location.getName());
			}
			//oaClient.analyzeUrl(url, Analysis.ALL);
			//oaClient.searchText(input, Analysis.ALL, searchTermsList);
			//oaClient.searchUrl(url, Analysis.ALL, searchTermsList);
		}
		catch (OpenAmplifyClientException oace)
		{
			oace.printStackTrace();
		}
	}
}
