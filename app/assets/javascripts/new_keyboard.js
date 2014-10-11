/**
 * Created by simeon on 10/10/14.
 */

jQuery(function($) {


    $(".new-keyboard-save").click(function(){
        $(this).addClass("disabled");
        $("#new_keyboard").submit();
    });


});
