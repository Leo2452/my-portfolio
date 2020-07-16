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

/** Adds a random greeting to the page. */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/** Prompt client for their name and give them a special greeting. */
function addSpecialGreeting() {
    var client = document.getElementById('name-input').value;
    fetch('/special-greeting').then(response => response.text()).then(greeting => {
        document.getElementById("special-greeting-container").innerText = 
        greeting + " " + client + ", welcome to my page :)";
    })
}

/** Makes sure user wants to return to previous page. */
function prevPage() {
    let returning = confirm('Returning to previous page');
    if(returning) {
        window.location = '/'
    }
}

 /** Redirect the user to the index page to determine authorization */
function redirect() {
    window.location.assign("/index.html");
}

/** Add a random fact to the page. */
function addRandomFact() {
     const facts = 
     ['I am currently trying to become fluent with my left hand!', 
     'The longest I\'ve been awake for is ~30 hours - \
     I do NOT recommend! Get sleep :)', 'I love to jump rope!', 
     'I like riding bikes', 'I love (most) seafood, from shrimp to oysters!'];

     //Pick a random fact
     const chosen = facts[Math.floor(Math.random() * facts.length)];

     //Add it to the page
     const factContainer = document.getElementById('fact-container');
     factContainer.innerText = chosen;
}

/** Prompts user with questionnaire to test whether they are humans or not. */
function questions() {
     //Provide message along with prompt and question
    alert("To access this page, you need to answer a simple question.\n" +
            "You will be given a basic prompt and must provide an answer " +
            "to determine if you are a human.");
    let ans = prompt("Hawks often hunt for small mammals like rabbits.\n" +
     	                "Who is the prey in this relationship?");

    //Redirect user to appropriate webpage
    if(ans.toLowerCase() === "rabbits" || ans.toLowerCase() === "rabbit") {
        window.location.assign("/homepage.html");
    } else {
        window.location.assign("/denied.html");
    }
}

/** Checks for valid int number input. Empties the current 
 *  comment history and reloads it with an updated number of comments.
 */
function updateComments() {
    var numCommentsString = document.getElementById("num-comments").value;
    var numCommentsFloat = Number.parseFloat(numCommentsString);
    if(!Number.isInteger(numCommentsFloat) || numCommentsFloat < 1) {
        alert("Please enter a positive integer.");
        return;
    }

    document.getElementById("comment-history").innerText = "";
    gatherComments().then((comments) => updateHomepage(comments));
}

/** Renders the amount of comments according to the user input. Creates
 *  and returns a list element containing the comments requested.
 */
function renderComments(commentData) {
    var numComments = document.getElementById("num-comments");
    if(numComments === null) {
        return;
    }
    const commentHistory = document.createElement("ul");
    commentHistory.setAttribute('id', "comment-history");
    for (i = 0; i < numComments.value; i++) {
        if (commentData.length > i) {
            commentHistory.appendChild(createListElement(commentData[i]));
        }
    }
    return commentHistory;
}

/** Loads comments and then renders them onscreen for user. */
function gatherComments() {
    return loadComments().then((commentData) => renderComments(commentData));
}

/** Calls CommentsServlet to fetch comment data, if authorized. */
function loadComments() {
    return fetch('/comments').then(response => response.json());
}

/** Creates an <li> element containing text. */
function createListElement(fullComment) {
  const liElement = document.createElement('li');
  liElement.innerText = fullComment.content + "\n" + "Submitted on " 
                        + fullComment.date + " by " + fullComment.email + "\n\n";
  return liElement;
}

/** Deletes all of the comment history inside the server's datastore. */
function deleteComments() {
    fetch("/delete-comments");
}

/** Shows comments onscreen with a div tag */
function updateHomepage(comments) {
    var commentArea = document.getElementById("comment-section");
    commentArea.innerText = "";
    commentArea.appendChild(comments);
}

/** Displays option for user to change login status. Gathers comments
 *  or hides them according to login status. Updates onscreen information.
 */
function display() {
    checkStatus().then((loginInfo) => {
        renderLoginForms(loginInfo);
        if(loginInfo.isLoggedIn) {
            return gatherComments();
        } else {
            return document.createElement("blank");
        }
    }).then((comments) => updateHomepage(comments));
}

/** Hide or display comment section if user is logged in. Create login
 *  forms to let user change login status.
 */
function renderLoginForms(loginInfo) {
    var welcomeBox = document.getElementById("welcome-box");
    if(!loginInfo.isLoggedIn) {
        var commentDisplay = document.getElementById("comment-display");
        commentDisplay.style.display = "none";
        showLoginStatus("Login", loginInfo.url, welcomeBox);
    } else {
        welcomeBox.innerTest = "Welcome, " + loginInfo.userEmail + "\n";
        showLoginStatus("Logout", loginInfo.url, welcomeBox);
    }
}

/** Check if a user is logged in to show comments along with their status. */
function checkStatus() {
    return fetch('/login').then(response => response.json());
}

/** Show option to log in or log out. */
function showLoginStatus(changeStatus, url, container) {
    var logoutReference = createAElement(url);
    container.innerText += changeStatus + " ";
    container.appendChild(logoutReference);
}

/** Creates an <a> element containing text. */
function createAElement(url) {
  const aElement = document.createElement('a');
  aElement.setAttribute('href', url);
  aElement.innerText = "here";
  return aElement;
}
