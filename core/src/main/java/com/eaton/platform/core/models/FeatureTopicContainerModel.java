package com.eaton.platform.core.models;

import javax.inject.Inject;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.annotation.PostConstruct;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import org.apache.sling.models.annotations.Via;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)


public class FeatureTopicContainerModel {
	
	 /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(FeatureTopicContainerModel.class);
	
	@Inject	@Via("resource")
	private Resource res;
	
	 /**
     * Inits the.
     */
    @PostConstruct
    protected void init() {
    	LOG.debug("FeatureTopicContainerModel :: init() :: Started");
		
		if (null != res) {
			final Node currentNode = res.adaptTo(Node.class);
			
			if(null != currentNode) {
					try {
							if(!currentNode.hasNode("featured-topic-title")) {
								currentNode.addNode("featured-topic-title");
							}
							if(!currentNode.hasNode("featured-topic-text")) {
								currentNode.addNode("featured-topic-text");
							}
					} catch (PathNotFoundException e) {
						LOG.error("PathNotFoundException  - ",e);
					} catch (RepositoryException e) {
						LOG.error("RepositoryException  - ",e);
					}
				}
		}
		LOG.debug(" FeatureTopicContainerModel :: int() :: Exit");
	}
	
}
