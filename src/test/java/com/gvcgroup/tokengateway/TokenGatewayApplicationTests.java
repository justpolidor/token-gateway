package com.gvcgroup.tokengateway;

import com.gvcgroup.tokengateway.controller.TokenController;
import com.gvcgroup.tokengateway.model.Token;
import com.gvcgroup.tokengateway.model.UserTokenState;
import com.gvcgroup.tokengateway.model.Username;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import javax.security.auth.Subject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TokenGatewayApplicationTests {

	@Autowired
    private TokenController tokenController;

    @Autowired
    private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
	    assertThat(tokenController).isNotNull();
	}

	@LocalServerPort
    private int port;

	private String invalidToken = "eyJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJ0b2tlbi1nYXRld2F5Iiwic3ViIjoianVzdGluIiwiaWF0IjoxNTEzMTgzNzM3LCJleHAiOjE1MTMxODM3OTd9.O_NREBdIxj4xq9dBQT5SrTg3Fvy5yh1KJIsnmbKFEaZHfBnlmtJ5CgN2S9sbYixYFauiPybujtqeDUtb8v1tCw";

	@Test
    public void shouldReturnValidToken() throws Exception {
        Username username = new Username("justin");
	    assertThat(this.restTemplate
                .postForObject("http://localhost:"+port+"/auth/generate",username, UserTokenState.class))
                .hasFieldOrPropertyWithValue("isValid",true);
    }


    @Test
    public void shouldCheckForValidToken() throws Exception {
        Username username = new Username("justin");

        UserTokenState userTokenStateGenerate = this.restTemplate.
                postForObject("http://localhost:"+port+"/auth/generate",username, UserTokenState.class);
        Token token = new Token(userTokenStateGenerate.getToken());
        UserTokenState userTokenStateCheck = this.restTemplate.
                postForObject("http://localhost:"+port+"/auth/check",token, UserTokenState.class);

        assertThat(userTokenStateCheck).hasFieldOrPropertyWithValue("isValid",true);
    }

    @Test
    public void shouldCheckForInvalidToken() throws Exception {
        Token token = new Token(invalidToken);
        UserTokenState userTokenStateCheck = this.restTemplate.
                postForObject("http://localhost:"+port+"/auth/check",token, UserTokenState.class);
        assertThat(userTokenStateCheck).hasFieldOrPropertyWithValue("isValid",false);
    }


}
