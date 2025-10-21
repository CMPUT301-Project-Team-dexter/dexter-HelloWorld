# ORGANIZER

| Responsibilities                                                                           | Collaborators           |
| ------------------------------------------------------------------------------------------ | ----------------------- |
| Create and update an event                                                                 | ==Event==               |
| Set ==Event== registration period                                                          | ==waitingList==         |
| Upload/Update ==Event== ==image==                                                          | ==attendeeList==        |
| Generate QR Code (==QRCodeService==) for an ==Event==                                      | ==lotteryService==      |
| View ==waitingList== for an ==Event==                                                      | ==QRCodeService==       |
| View ==attendeeList== for an ==Event==                                                     | ==notificationService== |
| Set number of attendees to sample from ==attendeeList==                                    | ==Image==               |
| Select random attendee to replace rejected invitation                                      |                         |
| Send ==notifications== to entrants on ==waitingList==, ==attendeeList==, ==cancelledList== |                         |
| Export final ==attendeeList==                                                              |                         |
| Manage ==Event== geolocation settings                                                      |                         |
| View map of entrant locations                                                              |                         |
