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

import java.util.List;

public class Topics
{
	public Domains domains = null;
	public List<Topic> topTopics = null;
	public List<Location> locations = null;
	public List<Topic> properNouns = null;
	/**
	 * @return the topTopics
	 */
	public List<Topic> getTopTopics()
	{
		return topTopics;
	}
	/**
	 * @param topTopics the topTopics to set
	 */
	public void setTopTopics(List<Topic> topTopics)
	{
		this.topTopics = topTopics;
	}
	/**
	 * @return the locations
	 */
	public List<Location> getLocations()
	{
		return locations;
	}
	/**
	 * @param locations the locations to set
	 */
	public void setLocations(List<Location> locations)
	{
		this.locations = locations;
	}
	/**
	 * @return the properNouns
	 */
	public List<Topic> getProperNouns()
	{
		return properNouns;
	}
	/**
	 * @param properNouns the properNouns to set
	 */
	public void setProperNouns(List<Topic> properNouns)
	{
		this.properNouns = properNouns;
	}
	/**
	 * @return the domains
	 */
	public Domains getDomains()
	{
		return domains;
	}
	/**
	 * @param domains the domains to set
	 */
	public void setDomains(Domains domains)
	{
		this.domains = domains;
	}
	
}
