// When page is loaded
document.addEventListener('DOMContentLoaded', function () {
    console.log("Ready.")

    const GOOGLE_SHEET_PATTERN = /^https:\/\/docs\.google\.com\/spreadsheets\/d\/[a-zA-Z0-9-_]+/;
    const GOOGLE_FORM_PATTERN = /^https:\/\/docs\.google\.com\/forms\/d\/e\/[a-zA-Z0-9-_]+/;


    document.getElementById('retrosheetForm').addEventListener('submit', async function (e) {
        e.preventDefault();

        const googleSheetUrl = document.getElementById('googleSheetUrl').value.trim();
        const sheetName = document.getElementById('sheetName').value.trim();
        const googleFormUrl = document.getElementById('googleFormUrl').value.trim();

        // Validation
        if (!googleSheetUrl && !googleFormUrl) {
            showError('Please provide either a Google Sheet URL or Google Form URL');
            return;
        }

        if (googleSheetUrl && !GOOGLE_SHEET_PATTERN.test(googleSheetUrl)) {
            showError('Invalid Google Sheet URL format. Please provide a valid Google Sheets URL.');
            return;
        }

        // When googleSheetUrl is present, sheetName should be required
        if (googleSheetUrl && !sheetName) {
            showError('Please provide the sheet name');
            return;
        }

        if (googleFormUrl && !GOOGLE_FORM_PATTERN.test(googleFormUrl)) {
            showError('Invalid Google Form URL format. Please provide a valid Google Forms URL.');
            return;
        }

        // Prepare payload
        const payload = {};

        if (googleSheetUrl) {
            payload.googleSheetUrl = googleSheetUrl;
            if (sheetName) {
                payload.sheetName = sheetName;
            }
        }

        if (googleFormUrl) {
            payload.googleFormUrl = googleFormUrl;
        }

        // Show loading state
        document.getElementById('loading').style.display = 'block';
        document.getElementById('submitBtn').disabled = true;
        document.getElementById('errorMessage').style.display = 'none';

        try {

            // let host = "http://localhost:8091/retrosheet-wizard"
            let host = "https://api.a64.in/retrosheet-wizard"

            // API url : POST http://localhost:8080/retrosheet-wizard/code (form data)
            /**
             * googleSheetUrl:https://docs.google.com/spreadsheets/d/12vMK4tdtpEbplmeg3Q3-qc3_yPKO92jp_o41wk4PYHg/edit?usp=sharing
             * googleFormUrl:https://docs.google.com/forms/d/e/1FAIpQLSdRUQAWjNw3EMFVaJkCqMISeOECht1FxYfq9JszpcPj2Qfkcg/viewform?usp=dialog
             * sheetName:marvel
             */
            const response = await fetch(host + '/code', {
                method: 'POST',
                // form data request
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: new URLSearchParams(payload),
            });

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            data = await response.json();
            console.log('Response:', data);


            // Hide form and show results
            document.getElementById('formContainer').style.display = 'none';
            document.getElementById('resultsContainer').style.display = 'block';

            // Populate code blocks
            document.getElementById('buildGradleCode').textContent = data.gradle || 'No build.gradle.kts content provided';
            document.getElementById('sheetCode').textContent = data.api || 'No sheet class content provided';
            document.getElementById('mainCode').textContent = data.main || 'No main class content provided';

            hljs.highlightAll()

            // Update sheet code title if sheetName is provided
            if (sheetName) {
                document.getElementById('sheetCodeTitle').textContent = `MyApi.kt`;
            }

        } catch (error) {
            console.error('Error:', error);
            showError('Failed to generate code. Please check your URLs and try again.');
        } finally {
            document.getElementById('loading').style.display = 'none';
            document.getElementById('submitBtn').disabled = false;
        }
    });

    function showError(message) {
        const errorElement = document.getElementById('errorMessage');
        errorElement.textContent = message;
        errorElement.style.display = 'block';
    }


    let isDebug = false;
    if(isDebug){
        fillDummyUrls()

        // Click the 'submitBtn'
        document.getElementById('submitBtn').click();
    }
});

