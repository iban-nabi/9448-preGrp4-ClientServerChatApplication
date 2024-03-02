# 2023-2_9448-preGrp4

## Prelim Group Project: Messaging Application
CIS-2-9448-IT222(Integrative Technologies Lab)

## Login, Session Management, User management and Security
◦ Each user must login to the system before he/she can perform actions/tasks.
◦ Management of the users must be done on the server application. Actions such as adding
a user or “banning” a user from logging in to the application can be done here.
◦ The application must be capable of managing sessions so that a user can only
communicate with the server using one transaction session (multiple logins are not
allowed) and within the limits of that session only. For example, a user cannot send a
message unless the user has properly logged in.

## Broadcast message (to all users)
◦ Users must be allowed to send a broadcast message to all other users that are logged in.
◦ Offline users will not be able to see previously broadcasted messages once they log in.
• Private message
◦ Any user must be allowed to send private messages to anyone. Offline users will be
notified of such message/s once they login.
◦ All private offline messages get to be stored into a file by the server application once it is
turned off and will be read again once the server is turned on.
◦ Offline messages that are already shown to the recipient will be deleted thereafter from
the side of the server.

## Bookmarking of contacts (“favorite” contacts/groups)
◦ Any user must be able to mark/bookmark frequently contacted users or groups for easy access. Note: bookmarked groups or contacts will be shown first in the list of users.
Alternatively bookmarked items can also be “unbookmarked”.

## Group conference (creation and invitation – similar to the group chat in Messenger)
◦ Any user must be capable of creating a conference and invite different users or groups to
join the conference. Other invited users may also invite other users but only the creator
of the conference is allowed to remove/kick users out of the conference.
◦ Note: the group conference will be available to the affected users even after restarting the
server.

## Searching Contacts/Groups
◦ Auxiliary feature for broadcasting messages, sending private messages, bookmarking of
users or groups and invitation to conferences.

## Other features
◦ Sending of files
◦ Editing of user profile (i.e. changing of user details such as the password. Note that
usernames must be unique or cannot be duplicated)
◦ Status updates (online, idle, away from keyboard, busy, etc.)
◦ You may look into existing chat applications for features that you may want to include in
your project.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.
