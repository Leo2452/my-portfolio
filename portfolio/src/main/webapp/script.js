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

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

/**
 * Prompt client for their name and give them a special greeting.
 */
function addSpecialGreeting() {
    var client = document.getElementById('name-input').value;
    fetch('/special-greeting').then(response => response.text()).then(greeting => {
        document.getElementById("special-greeting-container").innerText = 
        greeting + client + ", welcome to my page :)";
    })
}

/**
 * Makes sure user wants to return to previous page.
 */
function prevPage() {
    let returning = confirm('Returning to previous page');
    if(returning) {
        window.location = '/'
    }
}

 /**
  * Redirect the user to the index page to determine authorization
  */
  function redirect() {
      window.location.assign("/index.html");
  }

/**
 * Adds a random fact to the page
 */
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

/**
 * Prompts user with questionnaire to test whether they are
 * humans or not. 
 */
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

/**
 * Empties the current comment history and reloads it with a new 
 * number of comments to load from num-comments textbox
 */
function updateComments() {
    var numComments = document.getElementById("num-comments").value;
    if(numComments < 1 || (numComments - Number.parseInt(numComments)) !== 0) {
        alert("Please enter a positive integer.");
        return;
    }

    document.getElementById("comment-history").innerText = "";
    getComments();
}

/**
 * Fetches specified number of comments on webpage and 
 * displays a history of them.
 */
function getComments() {
    var numComments = document.getElementById("num-comments").value;
    fetch('/data').then(response => response.json()).then(comments =>{
        const history = document.getElementById("comment-history");
        for (i = 0; i < numComments; i++) {
            if (comments.length > i) {
               history.appendChild(createListElement(comments[i]));
            } else {
                alert("Displaying maximum comments: " + comments.length.toString());
                break;
            }
        }
    })
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

/*Deletes all of the comment history inside the server's datastore*/
function deleteComments() {
    fetch("/delete-data");
}