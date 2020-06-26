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
 