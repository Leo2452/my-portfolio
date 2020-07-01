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

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    private List<String> comments = new ArrayList<>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Query find = new Query("comment").addSort("timestamp", SortDirection.DESCENDING);
        DatastoreService data = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = data.prepare(find);

        //Construct an array of comment history
        List<String> history = new ArrayList<>();
        for (Entity entry: results.asIterable()) {
            String curr = (String) entry.getProperty("comment");
            history.add(curr);
        }

	    String json = convertToGson(history);
        response.setContentType("application/json;");
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String text = request.getParameter("input");
        comments.add(text);
        long timestamp = System.currentTimeMillis();

        //Create Entity of entry
        Entity entry = new Entity("comment");
        entry.setProperty("timestamp", timestamp);
        entry.setProperty("comment", text);

        //Put entry in datastore
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(entry);

        //Redirect user to the homepage
        response.sendRedirect("/homepage.html");
    }

    /*Converts an arrayList instance into a JSON string with Gson library.*/
    private String convertToGson(List<String> arr) {
        Gson conversion = new Gson();
        String result = conversion.toJson(arr);
        return result;
    }
}
