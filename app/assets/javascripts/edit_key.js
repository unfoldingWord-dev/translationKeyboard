
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
            $('input[type=text]').bind('mousedown.ui-disableSelection selectstart.ui-disableSelection', function(event) {
                event.stopImmediatePropagation();
            })
            //alert( "Load was performed." );
        });

    })

    $(".single-char").focus(function(){
       if($(this).val() == " "){
           $(this).val("");
       }
    });

};

function singleCharKeyUp() {
    var utf8Elem = $(this).parent().parent().parent().find(".single-utf8hex");
    var hex_code_value = ''
    if ($(this).val().length == 1) {
        utf8Elem.val($(this).val().charCodeAt(0).toString(16));
    }else if($(this).val().length > 1) {
        var char_array =  $(this).val().split('')
        for(var i=0; i<char_array.length; i++) {
            if (i != char_array.length -1) {
                hex_code_value += char_array[i].charCodeAt(0).toString(16);
                hex_code_value += ';';
            }else {
                hex_code_value += char_array[i].charCodeAt(0).toString(16);
            }
        }
        utf8Elem.val(hex_code_value);
    }else {
        utf8Elem.val("");
    }
}

function singleUtf8KeyUp(){
	
    var singleChar = $(this).parent().parent().parent().find(".single-char");
    if ($(this).val().indexOf(';') != -1 && $(this).val().length>0) {
        var segments = $(this).val().split(';');
        var char_hex = '';
        for(var i=0; i<segments.length;i++) {
            char_hex += String.fromCharCode(parseInt(segments[i], 16));

        }
        singleChar.val(char_hex);
    }else if($(this).val().length<=6 && $(this).val().length>0){
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
