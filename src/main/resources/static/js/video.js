function deleteDiv() {
    if(confirm("Do you wanted to delete this Video?")){
        const div = document.getElementById('item');
        div.remove();
    }
}
function uploadvideoDiv() {
    if(confirm("do you wanted to upload video?")){
        location.href='upload_page.html'
    }
}

////////////////////////////////////////////////////////////////
function copyDiv() {
    const list = document.getElementById('item_list');
    const testDiv = document.getElementById('item');
    const newNode = testDiv.cloneNode(true);
    newNode.id = 'item';
    list.prepend(newNode);
}

function uploadDiv() {
    if(confirm("Upload?")){
        location.href='myVideo_page.html'
        copyDiv();//이거를 불러와서 추가해주고 싶은데...이게 되어야 파일 드래그 이런 것도 될 것 같은데....
    }
}
function gotomyvideoDiv() {
    if(confirm("do you wanted to quit?")){
        location.href='myVideo_page.html'
    }
}
function gotouploadDiv() {
    if(confirm("do you wanted to upload video again?")){
        location.href='upload_page.html'
    }
}

////////////////////////////////////////////////////////////////
function nextDiv() {
    if(confirm("go to next step?")){
        location.href='uploading_page.html'
    }
}

function is_checked_youtube() {
    const checkbox = document.getElementById('my_checkbox_youtube');
    const is_checked_youtube = checkbox.checked;
}
function is_checked_tiktok() {
    const checkbox = document.getElementById('my_checkbox_tiktok');
    const is_checked_tiktok = checkbox.checked;
}
function is_checked_instagram() {
    const checkbox = document.getElementById('my_checkbox_instagram');
    const is_checked_instagram = checkbox.checked;
}
function is_checked_facebook() {
    const checkbox = document.getElementById('my_checkbox_facebook');
    const is_checked_facebook = checkbox.checked;
    //is_checked_facebook=true or false
}
