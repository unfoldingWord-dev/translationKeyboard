// This is a manifest file that'll be compiled into application.js, which will include all the files
// listed below.
//
// Any JavaScript/Coffee file within this directory, lib/assets/javascripts, vendor/assets/javascripts,
// or vendor/assets/javascripts of plugins, if any, can be referenced here using a relative path.
//
// It's not advisable to add code directly here, but if you do, it'll appear at the bottom of the
// compiled file.
//
// Read Sprockets README (https://github.com/sstephenson/sprockets#sprockets-directives) for details
// about supported directives.
//
//= require jquery
//= require jquery_ujs
//= require turbolinks
//= require_tree .
//= require bootstrap
//= require edit_key

ready = function(){

    var btnClick = function(){

        var currentText = $(".type-area-textarea").val();
        var newText;
        if($(this).attr("data-char")){
            newText = $(this).attr("data-char");
        } else {
            newText = $(this).html();
        }
        if(newText != " "){
            newText = newText.trim()
        }
        $(".type-area-textarea").val(currentText + newText);
    };
    /*
    $(".shift-btn").mousedown(function(){
        $(".keyboard .row .col-xs-1 > .btn").each(function(){
            var shiftChar = $(this).parent().find(".shift").text();
            $(this).text(shiftChar);
        });
    });
    $(".shift-btn").mouseup(function(){
        $(".keyboard .row .col-xs-1 > .btn").each(function(){
            var defaultChar = $(this).parent().find(".default-char").text();
            $(this).text(defaultChar);
        });
    });
    */
    $(".shift-btn").click(function(){
        if(!$(this).hasClass("btn-danger")){
            $(".keyboard .row .col-xs-1 > .default-key").each(function(){
                var shiftChar = $(this).parent().find(".shift").text();
                $(this).text(shiftChar);
            });
            $(".shift-btn").addClass("btn-danger");
        } else {
            $(".keyboard .row .col-xs-1 > .default-key").each(function(){
                var defaultChar = $(this).parent().find(".default-char").text();
                $(this).text(defaultChar);
            });
            $(".shift-btn").removeClass("btn-danger");
        }
    });

    $(".col-xs-1").has(".long-press").each(function(){
        $(this).find(".long").popover({
            html:true,
            placement:'top',
            title:"Long Press",
            trigger: 'focus',
            content:function(){
                return $(this).parent().find(".long-press").html();
            }
        }).parent().delegate(".popover .btn", "click", btnClick);
    });

    $(".keypress:not(.shift-btn)").click(btnClick);

    $("#selected_keyboard_variant_id").change(function(){
        window.location = "/keyboard/get_keyboard_variant/" + $(this).val();
    });

    $(".edit-key-btn").hide();

    $(".edit-keyboard").click(function(event){
        event.preventDefault();
        if($(this).hasClass("btn-primary")){
           $(this).removeClass("btn-primary");
           $(this).addClass("btn-success");
           $(this).text("Done");
           $(".edit-key-btn").show();
       } else {
           $(this).removeClass("btn-success");
           $(this).addClass("btn-primary");
           $(this).text("Edit Keyboard");
           $(".edit-key-btn").hide();
       }
        return false;
    });



};

jQuery(document).ready(ready);
jQuery(document).on('page:load', ready);