# CHANGELOG

## 3269-18Mar
- While creating a new account, facilities located nearby are automatically suggested 
- Scheduling an appointment shows new date options 

## 2856-25Feb
- Updated illustration on the patients tab 
- Improved app performance during first sync of patient data 
- Improve design of medical history screen 

## 2605-04Feb
- Blood pressures can be entered for dates in the past 
- New prescription drugs screen design 
- New medicine dosage picker design 

## 2301-04Jan
- Age and date of birth fields removed from patient search screen 
- Improved patient search algorithm
- Added new cancellation reasons for appointments 
- Updated the rules for identifying high-risk patients

## 2204-26Dec
- Fix: medical history syncing broke for some users

## 2116-17Dec
- Automatically submit PIN when 4 digits have been entered
- Show Yes/No/Unanswered buttons on medical history questions 
- Show option to change clinics from the home screen 
- Ask for confirmation when exiting Edit Patient screen without saving the edited information

## 2052-10Dec
- Dismiss keyboard while scrolling list of clinics 
- Highlight search query term when filtering clinics 

## 1991-03Dec
- Added ability to request a new OTP via SMS, while logging in 
- Blood pressure values are now editable for 24 hours
- Demographic data of patients is now editable
- "Very High" and "Extremely High" BPs are now shows as "High" 
- PIN entry is now protected against brute force attacks
- High risk patients are labelled in the overdue list
- Fix: crash when app was closed before entering OTP, while logging in 

## 1705-10Nov
- Patient searches with more than 100 results caused a crash
- Months in a date were being ignored, when calculating fuzzy age bounds

## 1678-08Nov
- Patient entry UI was cleaned up; entering a colony is now mandatory.
- Visual updates to the patient summary and home screen.
- Patients can be marked as "Dead" on the overdue screen, after which they will stop showing up on the overdue screen.
- Added option to schedule appointment for one month.
- Change age search to be more permissive for older patients.
- Sync frequency has been increased to once every fifteen minutes.
- Fix: Occasionally certain records did not get synced to the server.

## 1462-16Oct
- Add a helpful message to the home screen
- Fix: calling a patient from the overdue list sometimes used an incorrect phone number 

## 1420-11Oct
- Order facilities alphabetically in lists

## 1377-09Oct
- Fix: crash on opening patient summary if there was a BP recorded more than 6 months ago

## 1356-07Oct
- Fix: crash if multiple histories are present for the same patient 
- Fix: crash if empty systolic or diastolic blood pressure value is submitted
 
