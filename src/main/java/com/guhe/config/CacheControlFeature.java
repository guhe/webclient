package com.guhe.config;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.core.HttpHeaders;

import com.guhe.webclient.CacheControl;

class CacheControlFeature implements DynamicFeature {

	private static final CacheResponseFilter NO_CACHE_FILTER = new CacheResponseFilter("no-cache");

	@Override
	public void configure(ResourceInfo resourceInfo, FeatureContext context) {
		CacheControl cch = resourceInfo.getResourceMethod().getAnnotation(CacheControl.class);
		if (cch == null) {
			context.register(NO_CACHE_FILTER);
		} else {
			context.register(new CacheResponseFilter(cch.value()));
		}
	}

	private static class CacheResponseFilter implements ContainerResponseFilter {
		private final String headerValue;

		CacheResponseFilter(String headerValue) {
			this.headerValue = headerValue;
		}

		@Override
		public void filter(ContainerRequestContext reqContext, ContainerResponseContext rspContext) {
			rspContext.getHeaders().putSingle(HttpHeaders.CACHE_CONTROL, headerValue);
			if (this == NO_CACHE_FILTER) {
				rspContext.getHeaders().putSingle(HttpHeaders.EXPIRES, 0);
			}
		}

	}
}
