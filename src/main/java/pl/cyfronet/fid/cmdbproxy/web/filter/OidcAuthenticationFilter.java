package pl.cyfronet.fid.cmdbproxy.web.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import pl.cyfronet.fid.cmdbproxy.util.CollectionUtil;

@Component
@Order(1)
public class OidcAuthenticationFilter extends OncePerRequestFilter {

	public static final String ROLE_USER = "ROLE_USER";

	public static final String ROLE_ADMIN = "ROLE_ADMIN";

	private static final String BEARER_REGEXP = "\\ABearer (.*)\\z";

	private static final Logger log = LoggerFactory.getLogger(OidcAuthenticationFilter.class);

	@Value("${oidc.userinfo}")
	private String userInfo;

	@Value("${proxy.cmdb-crud.admin_group}")
	private String adminGroupName;

	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class TokenDetails {
		@JsonProperty("sub")
		String sub;

		@JsonProperty("groups")
		List<String> groups;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

		String authorizationHeader = Optional.ofNullable(request.getHeader("Authorization")).orElse("");
		log.debug("Authorization: {}", authorizationHeader);
		log.debug("from {}", request.getRemoteAddr());

		Matcher m = Pattern.compile(BEARER_REGEXP).matcher(authorizationHeader);
		if (m.find()) {
			verifyAuthorization(m.group(1));
		}

		filterChain.doFilter(request, response);
	}

	private void verifyAuthorization(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Bearer " + token);
		HttpEntity<String> entity = new HttpEntity<>(headers);

		try {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<TokenDetails> exchange = restTemplate.exchange(userInfo, HttpMethod.GET, entity, TokenDetails.class);
			TokenDetails details = exchange.getBody();

			SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(details.sub,
			        token, AuthorityUtils.commaSeparatedStringToAuthorityList(getUserRole(details))));
			log.debug("User logged in as: {}", details.sub);
		} catch (Exception e) {
			log.debug("Error while getting user info", e);
		}
	}

	private String getUserRole(TokenDetails tokenDetails) {
		return CollectionUtil.notNullable(tokenDetails.groups).contains(adminGroupName) ? ROLE_ADMIN : ROLE_USER;
	}
}
