

readyEditKey = function() {
    $(".single-char").on('input', singleCharKeyUp);
    $(".single-utf8hex").on('input', singleUtf8KeyUp);

    $(".save-key").click(function(){
        $(this).addClass("disabled");
        $("#edit_key_position_"+$(this).data("keypos")).submit();
    });

    $(".edit-key-btn").hide();

    $(".delete-key-char").click(deleteChar);

    $(".add-character-btn").click(function(){
        var addBtn = $(this);
        $.get( "/characters/new_block", function( data ) {
            addBtn.parent().parent().before( data );
            addBtn.parent().parent().parent().find(".single-char").unbind("on");
            addBtn.parent().parent().parent().find(".single-utf8hex").unbind("on");
            addBtn.parent().parent().parent().find(".delete-key-char").unbind("click");
            addBtn.parent().parent().parent().find(".single-char").on('input', singleCharKeyUp);
            addBtn.parent().parent().parent().find(".single-utf8hex").on('input', singleUtf8KeyUp);
            addBtn.parent().parent().parent().find(".delete-key-char").click(deleteChar);
            //alert( "Load was performed." );
        });

    })
};

function singleCharKeyUp(){
    var utf8Elem = $(this).parent().parent().parent().find(".single-utf8hex");
    if($(this).val().length==1){
        utf8Elem.val($(this).val().charCodeAt(0).toString(16));
    } else {
        utf8Elem.val("");
    }
}

function singleUtf8KeyUp(){
    var singleChar = $(this).parent().parent().parent().find(".single-char");
    if($(this).val().length<=6 && $(this).val().length>0){
        singleChar.val(String.fromCharCode(parseInt($(this).val(), 16)));
    } else {
        singleChar.val("");
    }
}

function deleteChar(){
    $(this).parent().parent().parent().remove();
}

jQuery(document).ready(readyEditKey);
jQuery(document).on('page:load', readyEditKey);