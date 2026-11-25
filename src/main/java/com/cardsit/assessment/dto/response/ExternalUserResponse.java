package com.cardsit.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO for JSONPlaceholder {@code /users/{id}} responses.
 *
 * <p>{@link JsonIgnoreProperties} ignores unknown fields (address, company, etc.)
 * so we only bind the fields we care about — safer if the remote schema adds properties.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalUserResponse {

    private Long id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String website;

    public ExternalUserResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
}
