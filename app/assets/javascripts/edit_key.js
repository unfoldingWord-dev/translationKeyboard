

readyEditKey = function() {
    $(".single-char").keyup(function(){
       var utf8Elem = $(this).parent().parent().parent().find(".single-utf8hex");
       if($(this).val().length==1){
           utf8Elem.val($(this).val().charCodeAt(0).toString(16));
       } else {
           utf8Elem.val("");
       }
    });
    $(".single-utf8hex").keyup(function(){
        var singleChar = $(this).parent().parent().parent().find(".single-char");
        if($(this).val().length<=6 && $(this).val().length>0){
            singleChar.val(String.fromCharCode(parseInt($(this).val(), 16)));
        } else {
            singleChar.val("");
        }
    });

    $(".save-key").click(function(){
        $(this).addClass("disabled");
        $("#edit_key_position_"+$(this).data("keypos")).submit();
    });

    $(".edit-key-btn").hide();
};

jQuery(document).ready(readyEditKey);
jQuery(document).on('page:load', readyEditKey);