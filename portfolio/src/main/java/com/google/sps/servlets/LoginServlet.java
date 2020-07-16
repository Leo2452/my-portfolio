// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.data.LoginInfo;
import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that grabs a user's login status along with their
 *  link to login or logout and their email.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");
        boolean isLoggedIn;
        String url;
        String userEmail;

        UserService credentials = UserServiceFactory.getUserService();
        if(credentials.isUserLoggedIn()) {
            isLoggedIn = true;
            url = credentials.createLogoutURL("/homepage.html");
            userEmail = credentials.getCurrentUser().getEmail();
        } else {
            isLoggedIn = false;
            url = credentials.createLoginURL("/homepage.html");
            userEmail = "";
        }

        LoginInfo userLogin = new LoginInfo(isLoggedIn, url, userEmail);
        response.getWriter().println(gson.toJson(userLogin));
    }
}
