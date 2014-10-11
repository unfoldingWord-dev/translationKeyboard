/**
 * Created by simeon on 10/10/14.
 */

readyNewKeyboard = function() {


    $(".new-keyboard-save").click(function(){
        $(this).addClass("disabled");
        $("#new_keyboard").submit();
    });


};


jQuery(document).ready(readyNewKeyboard);
jQuery(document).on('page:load', readyNewKeyboard);