# PIET Campus Hub (Java desktop app)

A student portal for Poornima Institute of Engineering & Technology, Jaipur, written in plain Java (Swing).
No Maven, no Gradle, no internet download. It only needs a JDK (version 17 or newer).

## Open and run in IntelliJ IDEA
1. Unzip the file. You get a folder called `PIETCampusHub`.
2. In IntelliJ: **File > Open**, select the `PIETCampusHub` folder, click OK, then **Trust Project**.
3. If IntelliJ shows "Project JDK is not defined" or "Setup SDK" at the top, click it and pick any JDK 17 or newer.
   (Or go to File > Project Structure > Project > SDK and choose your JDK.)
4. At the top right, the run configuration **PIET Campus Hub** is already selected. Click the green Run button.
   You can also open `src/com/piet/campushub/Main.java` and click the green arrow next to `main`.
5. Sign in with **Riya Suthar** / **9216@riya**. Type the username, press **Enter** to jump to the password box, then press **Enter** again to sign in.

## One file per page (src/com/piet/campushub/pages)
| Page | File |
|---|---|
| Sign in | LoginPage.java |
| Home | HomePage.java |
| Attendance (Date, Month, Subject tabs with skip slider) | AttendancePage.java |
| Class Schedule | SchedulePage.java |
| Internal Marks | MarksPage.java |
| Results | ResultsPage.java |
| Fees | FeesPage.java |
| Leave | LeavePage.java |
| Calendar | CalendarPage.java |
| Settings | SettingsPage.java |

## Other folders
- `Main.java`: starts the app.
- `ui/`: the window (AppFrame), side menu (Sidebar), top bar (TopBar), colours (Theme), animations (Anim),
  pop-up messages (Toast), profile photo (Avatar) and drawing helpers (RoundedPanel, RoundButton, RingChart,
  ProgressBar, CampusImage, Page, UI).
- `data/`: subjects, teachers, timetable and holidays (CollegeData.java), attendance (AttendanceService.java),
  results, fees, leave requests and calendar events. **Edit CollegeData.java to use real data.**
- `img/campus.jpg`, `img/campus_blur.jpg`: the campus photo and the blurred copy behind sign-in.
- `img/profile.jpg`: the profile photo. Replace this file (square image) to change it.

## Interactive touches
- Click your photo in the side menu to open Settings; double-click it to go back to Home.
- Theme: Sapphire & Pearl (dark and light). Pages glide in when you open them.
- Cards light up and lift when the mouse is over them.
- Short messages slide up at the bottom (for example after saving leave or adding an event).
- The countdown to mid-terms ticks every second. Wrong sign-in shakes the card.

## Notes
- Subjects, teachers, college hours (8:00 AM to 2:50 PM, lunch 12:00 to 12:50) and the 29 Oct mid-term date are real.
  Attendance, marks, results and fees are sample data.
- Leave requests and your own calendar events are kept while the app is open, and reset when you close it.
- The password check is for practice only.
