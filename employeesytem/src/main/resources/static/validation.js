function resetErrors() {
    document.querySelectorAll('.error').forEach(el => el.textContent = '');
}

function displayError(id, message) {
    const errorElement = document.getElementById(id + "Error");
    if (errorElement) {
        errorElement.textContent = message;
    }
}

/**
 * Client-Side Validation Logic
 * @param {object} employee - The employee data object
 * @returns {boolean} - True if validation passes
 */
function validateEmployee(employee) {
    resetErrors();
    let isValid = true;

    // --- Constraints enforced by Backend Validation ---

    // 1. Name Length (Min 2, Max 50)
    if (employee.firstName.trim().length < 2 || employee.firstName.trim().length > 50) {
        displayError('firstName', 'First name must be between 2 and 50 characters.');
        isValid = false;
    }
    if (employee.lastName.trim().length < 2 || employee.lastName.trim().length > 50) {
        displayError('lastName', 'Last name must be between 2 and 50 characters.');
        isValid = false;
    }

    // 2. Email Format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(employee.email.trim())) {
         displayError('email', 'Please enter a valid email format.');
         isValid = false;
    }

    // 3. Department (Check against predefined list/enum)
    const validDepartments = ["HR", "IT", "MARKETING", "SALES", "FINANCE"];
    if (!employee.department || !validDepartments.includes(employee.department.toUpperCase())) {
        displayError('department', 'Department selection is required and must be a valid option.');
        isValid = false;
    }

    // 4. Salary (Numeric, Min 1, Max, and Precision)
    const salaryValue = parseFloat(employee.salary);
    if (isNaN(salaryValue) || salaryValue < 1) {
        displayError('salary', 'Salary must be a positive number.');
        isValid = false;
    } else if (salaryValue > 10000000) { // Max constraint (matching backend @Max)
        displayError('salary', 'Salary cannot exceed 10,000,000.');
        isValid = false;
    } else {
        // Check for more than 2 decimal places (precision)
        const parts = salaryValue.toString().split('.');
        if (parts.length > 1 && parts[1].length > 2) {
             displayError('salary', 'Salary can only have up to two decimal places.');
             isValid = false;
        }
    }

    return isValid;
}

/**
 * Handles error responses from the backend (Spring Validation 400 Bad Request)
 * @param {Response} res - The Fetch API Response object
 * @returns {Promise<boolean>} - True if validation errors were processed
 */
async function handleBackendValidationErrors(res) {
    // Check if the response is a 400 Bad Request (Validation failure)
    if (res.status === 400) {
        resetErrors();
        const errors = await res.json();
        document.getElementById("generalError").textContent = "Validation failed. See field errors below.";

        // Display errors returned from the GlobalExceptionHandler (e.g., {"firstName": "is required"})
        for (const field in errors) {
            displayError(field, errors[field]);
        }
        return true; // Indicates validation error was handled
    } else if (res.status === 409 || res.status === 404) {
        // Handle Conflict (409) or Not Found (404) errors
        const errorMsg = await res.text();
        document.getElementById("generalError").textContent = "Error: " + errorMsg;
        return true;
    }

    // Clear general errors if a different status occurred
    document.getElementById("generalError").textContent = "An unexpected error occurred.";
    return false; // Indicates error was not a validation/expected API error
}