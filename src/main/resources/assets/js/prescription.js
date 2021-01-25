$(document).ready(function () {
    $(document).on("click", ".btn-add", function () {

        let myInput = $(this).parent().find("input");
        let invalid = myInput.filter(function () {
            return $(this).val().trim().length === 0;
        });

        if (invalid.length > 0) {
            invalid.addClass("is-invalid");
            return;
        } else {
            myInput.removeClass("is-invalid");
        }

        let newEntry = $(this).parent().clone();
        $(newEntry).find("input").prop("disabled", true);
        $(newEntry).find("button.btn-add")
            .removeClass("btn-add")
            .removeClass("btn-success")
            .addClass("btn-secondary")
            .addClass("btn-remove")
            .text("-");

        myInput.val("");
        $(this).parent().before(newEntry);
    });

    $(document).on("click", ".btn-remove", function () {
        $(this).parent().empty().remove();
    });
});
