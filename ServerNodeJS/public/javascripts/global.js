/**
 * Created by roger.jaggi on 21.03.2014.
 */

// Userlist data array for filling in info box
var userListData = [];

// DOM Ready =============================================================
$(document).ready(function() {

    // Populate the user table on initial page load
    populateTable();

    // Username link click
    $('#userList').find("table tbody").on('click', 'td a.linkshowuser', showUserInfo);

    // Add User button click
    $('#btnAddUser').on('click', addUser);

    // Delete User link click
    $('#userList table tbody').on('click', 'td a.linkdeleteuser', deleteUser);

});

// Functions =============================================================

// Fill table with data
function populateTable() {

    // Empty content string
    var tableContent = '';

    // jQuery AJAX call for JSON
    $.getJSON( '/users', function( data ) {

        // Stick our user data array into a userlist variable in the global object
        userListData = data;

        // For each item in our JSON, add a table row and cells to the content string
        $.each(data, function(){
            tableContent += '<tr>';
            tableContent += '<td><a href="#" class="linkshowuser" rel="' + this._id + '" title="Show Details">' + this._id + '</td>';
            tableContent += '<td>' + this.displayName + '</td>';
            tableContent += '<td>' + this.currentLatitude + '</td>';
            tableContent += '<td>' + this.currentLongitude + '</td>';
            tableContent += '<td><a href="#" class="linkdeleteuser" rel="' + this._id + '">delete</a></td>';
            tableContent += '</tr>';
        });

        // Inject the whole content string into our existing HTML table
        $('#userList').find("table tbody").html(tableContent);
    });
}

// Show User Info
function showUserInfo(event) {

    // Prevent Link from Firing
    event.preventDefault();

    // Retrieve username from link rel attribute
    var thisUserId = $(this).attr('rel');

    // Get Index of object based on id value
    var arrayPosition = userListData.map(function (arrayItem) {
        return arrayItem._id;
    }).indexOf(thisUserId);

    // Get our User Object
    var thisUserObject = userListData[arrayPosition];

    //Populate Info Box
    $('#userId').text(thisUserObject._id);
    $('#userDisplayName').text(thisUserObject.displayName);
    $('#userCurrentLatitude').text(thisUserObject.currentLatitude);
    $('#userCurrentLongitude').text(thisUserObject.currentLongitude);
    $('#userPositionReportedAt').text(getStringFromDate(new Date(thisUserObject.positionReportedAt)));
    $('#userCreatedAt').text(getStringFromDate(new Date(thisUserObject.createdAt)));
    $('#userUpdatedAt').text(getStringFromDate(new Date(thisUserObject.updatedAt)));

}

// Add User
function addUser(event) {
    event.preventDefault();

    // Super basic validation - increase errorCount variable if any fields are blank
    var errorCount = 0;
    $('#addUser').find("input").each(function(index, val) {
        if($(this).val() === '') { errorCount++; }
    });

    // Check and make sure errorCount's still at zero
    if(errorCount === 0) {

        // If it is, compile all user info into one object
        var newUser =new Object();
        newUser.displayName = $("#addUser").find("fieldset input#inputDisplayName").val();
        newUser.currentLatitude = $("#addUser").find("fieldset input#inputCurrentLatitude").val();
        newUser.currentLongitude = $("#addUser").find("fieldset input#inputCurrentLongitude").val();

        // Use AJAX to post the object to our adduser service
        $.ajax({
            type: 'POST',
            data: newUser,
            url: '/users',
            dataType: 'JSON'
        }).done(function( response ) {

            // Check for successful (blank) response
            if (response.msg === '') {

                // Clear the form inputs
                $('#addUser').find("fieldset input").val('');

                // Update the table
                populateTable();

            } else {

                // If something goes wrong, alert the error message that our service returned
                alert('Error: ' + response.msg);

            }
        });
    }
    else {
        // If errorCount is more than 0, error out
        alert('Please fill in all fields');
        return false;
    }
}

// Delete User
function deleteUser(event) {

    event.preventDefault();

    // Pop up a confirmation dialog
    var confirmation = confirm('Are you sure you want to delete this user?');

    // Check and make sure the user confirmed
    if (confirmation === true) {

        // If they did, do our delete
        $.ajax({
            type: 'DELETE',
            url: '/users/' + $(this).attr('rel')
        }).done(function( response ) {

            // Check for a successful (blank) response
            if (response.msg === '') {
            }
            else {
                alert('Error: ' + response.msg);
            }

            // Update the table
            populateTable();

        });

    }
    else {

        // If they said no to the confirm, do nothing
        return false;

    }
}

function getStringFromDate(theDate) {
    var year    = theDate.getFullYear();
    var month   = theDate.getMonth()+1;
    var day     = theDate.getDate();
    var hour    = theDate.getHours();
    var minute  = theDate.getMinutes();
    var second  = theDate.getSeconds();
    if(month.toString().length == 1) {
        var month = '0'+month;
    }
    if(day.toString().length == 1) {
        var day = '0'+day;
    }
    if(hour.toString().length == 1) {
        var hour = '0'+hour;
    }
    if(minute.toString().length == 1) {
        var minute = '0'+minute;
    }
    if(second.toString().length == 1) {
        var second = '0'+second;
    }
    var dateTime = year+'/'+month+'/'+day+' '+hour+':'+minute+':'+second;
    return dateTime;
}