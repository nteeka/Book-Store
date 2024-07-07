document.addEventListener('DOMContentLoaded', function() {

        setTimeout(function() {
            var successMessage = document.getElementById('successMessage');
            if (successMessage) {
                successMessage.style.display = 'none';
            }

            var errorMessage = document.getElementById('errorMessage');
            if (errorMessage) {
                errorMessage.style.display = 'none';
            }
        }, 5000); // 5000 milliseconds = 5 seconds

    document.querySelector('tbody').addEventListener('click', function(event) {
        if (event.target.classList.contains('editButton')) {
            var button = event.target;
            var id = button.getAttribute('data-id');
            var name = button.getAttribute('data-name');

            // Set the values in the modal
            document.getElementById('idEdit').value = id;
            document.getElementById('nameEdit').value = name;
        }
    });
    // Handle the enable/disable button click
        document.querySelector('tbody').addEventListener('click', function(event) {
            if (event.target.classList.contains('enableDisableButton')) {
                event.preventDefault(); // Prevent the default action

                var action = event.target.textContent.trim();
                var confirmMessage = `Are you sure you want to ${action.toLowerCase()} this category?`;

                if (confirm(confirmMessage)) {
                    window.location.href = event.target.href; // Navigate to the href
                }
            }
        });
});