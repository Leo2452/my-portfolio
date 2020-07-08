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

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.sps.data.Comment;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Servlet that contains, saves and gets comments from database. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private List<String> comments = new ArrayList<>();
    private final Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query find = new Query("comment").addSort("date", SortDirection.DESCENDING);
        DatastoreService data = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = data.prepare(find);

        //Construct an array of comment history
        List<Comment> history = new ArrayList<>();
        for (Entity entry: results.asIterable()) {
            String curr = (String) entry.getProperty("text");
            Date time = (Date) entry.getProperty("date");
            String user = (String) entry.getProperty("email");
            double score = (double) entry.getProperty("score");

            history.add(new Comment(curr, time, user, score));
        }

        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(history));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String text = request.getParameter("input");
        comments.add(text);
        Date date = new Date();
        UserService credentials = UserServiceFactory.getUserService();

        Document doc =
            Document.newBuilder().setContent(text).setType(Document.Type.PLAIN_TEXT).build();
        LanguageServiceClient languageService = LanguageServiceClient.create();
        Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        double score = sentiment.getScore();
        languageService.close();

        //Create Entity of entry
        Entity entry = new Entity("comment");
        entry.setProperty("date", date);
        entry.setProperty("text", text);
        entry.setProperty("email", credentials.getCurrentUser().getEmail());
        entry.setProperty("score", score);

        //Put entry in datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(entry);

        //Redirect user to the homepage
        response.sendRedirect("/homepage.html");
    }
}
