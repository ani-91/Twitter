import requests
import json

BASE_URL = "http://localhost:8080/"  # Replace with your actual base URL

def test1():
    data = { "email": "f20202231@hyderabad.bits-pilani.ac.in", "name": "Aarush", "password": "123" }
    response = requests.post(BASE_URL + 'signup', json=data)
    if response.text == "Account Creation Successful":
        print("test1: Correct")
    else:
        print("test1: Incorrect")

def test2():
    data = { "email": "f20202231@hyderabad.bits-pilani.ac.in","name": "Aarush","password": "123"}
    response = requests.post(BASE_URL + 'signup', 
    json = data)

    if(response.text == "Forbidden, Account already exists" or json.loads(response.text)["Error"] == "Forbidden, Account already exists"):
        print("Correct")
    else:
        print("Incorrect")


def test3():
    data = { "email": "f20202231@hyderabad.bits-pilani.ac.in", "password": "123" }
    response = requests.post(BASE_URL + 'login', json=data)
    if response.text == "Login Successful":
        print("test3: Correct")
    else:
        print("test3: Incorrect")

def test4():
    data = { "email": "f20202231@hyderabad.bits-pilani.ac.in", "password": "1243" }
    response = requests.post(BASE_URL + 'login', json=data)
    if response.text == "Username/Password Incorrect" or json.loads(response.text).get("Error") == "Username/Password Incorrect":
        print("test4: Correct")
    else:
        print("test4: Incorrect")

def test5():
    data = { "email": "f20202232@hyderabad.bits-pilani.ac.in", "name": "AarushS", "password": "123" }
    response = requests.post(BASE_URL + 'signup', json=data)
    if response.text == "Account Creation Successful":
        response = requests.get(BASE_URL + 'users')
        users = json.loads(response.text)
        if len(users) == 2 and (users[0]["userID"] == 1 or users[0]["userID"] == 2):
            print("test5: Correct")
        else:
            print("test5: Incorrect")
    else:
        print("test5: Incorrect")

def test6():
    data = { "postBody": "This is a test post body.", "userID": 1 }
    response = requests.post(BASE_URL + 'post', json=data)
    if response.text == "Post created successfully":
        print("test6: Correct")
    else:
        print("test6: Incorrect")

def test7():
    data = { "postID": 1 }
    response = requests.get(BASE_URL + 'post', params=data)
    post = json.loads(response.text)
    if post["postID"] == 1 and post["postBody"] == "This is a test post body." and post["comments"] == []:
        print("test7: Correct")
    else:
        print("test7: Incorrect")

def test8():
    data = { "commentBody": "This is sample comment", "postID": 1, "userID": 2 }
    response = requests.post(BASE_URL + 'comment', json=data)
    if response.text == "Comment created successfully":
        print("test8: Correct")
    else:
        print("test8: Incorrect")

def test9():
    data = { "postID": 1 }
    response = requests.get(BASE_URL + 'post', params=data)
    post = json.loads(response.text)
    comment = post["comments"][0]
    if post["postID"] == 1 and comment["commentID"] == 1 and comment["commentBody"] == "This is sample comment" and comment["commentCreator"]["userID"] == 2:
        print("test9: Correct")
    else:
        print("test9: Incorrect")

def test10():
    data = { "commentID": 1, "commentBody": "This is an edited sample comment" }
    response = requests.patch(BASE_URL + 'comment', json=data)
    if response.text == "Comment edited successfully":
        print("test10: Correct")
    else:
        print("test10: Incorrect")

def test11():
    data = { "postID": 1 }
    response = requests.get(BASE_URL + 'post', params=data)
    post = json.loads(response.text)
    comment = post["comments"][0]
    if post["postID"] == 1 and comment["commentID"] == 1 and comment["commentBody"] == "This is an edited sample comment" and comment["commentCreator"]["userID"] == 2:
        print("test11: Correct")
    else:
        print("test11: Incorrect")

def test12():
    data = { "commentID": 3 }
    response = requests.delete(BASE_URL + 'comment', params=data)
    if response.text == "Comment does not exist" or json.loads(response.text).get("Error") == "Comment does not exist":
        print("test12: Correct")
    else:
        print("test12: Incorrect")

def test13():
    data = { "commentID": 1 }
    response = requests.delete(BASE_URL + 'comment', params=data)
    if response.text == "Comment deleted":
        print("test13: Correct")
    else:
        print("test13: Incorrect")

def test14():
    data = { "postID": 1, "postBody": "This is an edited test post body." }
    response = requests.patch(BASE_URL + 'post', json=data)
    if response.text == "Post edited successfully":
        print("test14: Correct")
    else:
        print("test14: Incorrect")

def test15():
    response = requests.get(BASE_URL)
    response_data = json.loads(response.text)

    posts = response_data.get('posts', [])  

    if len(posts) == 1:
        post = posts[0]  
        if post.get("postID") == 1 and post.get("postBody") == "This is an edited test post body.":
            print("test15: Correct")
        else:
            print("test15: Incorrect")
    else:
        print("test15: Incorrect")

def test16():
    data = { "postID": 1 }
    response = requests.delete(BASE_URL + 'post', params=data)
    if response.text == "Post deleted":
        print("test16: Correct")
    else:
        print("test16: Incorrect")

def test17():
    data = { "postID": 1 }
    response = requests.delete(BASE_URL + 'post', params=data)
    if response.text == "Post does not exist" or json.loads(response.text).get("Error") == "Post does not exist":
        print("test17: Correct")
    else:
        print("test17: Incorrect")

# List of all test functions
tests = [test1, test2, test3, test4, test5, test6, test7, test8, test9, test10, test11, test12, test13, test14, test15, test16, test17]

def main():
    for i, test in enumerate(tests, start=1):
        print(f"Running test case #{i}")
        test()

if __name__ == "__main__":
    main()
