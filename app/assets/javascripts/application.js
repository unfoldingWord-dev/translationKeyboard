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
//= require_tree .
//= require bootstrap
//= require bootstrap-select
//= require edit_key
//= require new_keyboard

ready = function(){

    var btnClick = function() {

        var currentText = $(".type-area-textarea").val();
        var newText;
        if ($(this).attr("data-char")) {
            newText = $(this).attr("data-char");
        } else {
            newText = $(this).html();
        }

        if (newText != " ") {
            newText = newText.trim()
        }
        if (newText == "&nbsp;") {
            return; // do nothing
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
        window.location = "/keyboard/variant/" + $(this).val();
    });

    $(".edit-key-btn").hide();

    $(".edit-keyboard").click(function(event){
        event.preventDefault();
        $(".keypress:not(.shift-btn)").unbind("click");
        if($(this).hasClass("btn-primary")){
           $(this).removeClass("btn-primary");
           $(this).addClass("btn-success");
           $(this).text("Done");
           $(".shift-space-row").hide();
           $(".type-area").hide();
           $(".long").hide();
           $(".keypress:not(.shift-btn)").removeClass('btn-default');
           $(".keypress:not(.shift-btn)").addClass('btn-info');
           $(".keypress:not(.shift-btn)").click(function(){
               $($(this).data("target")).modal('show');
           });
       } else {
           $(this).removeClass("btn-success");
           $(this).addClass("btn-primary");
           $(this).text("Edit Keyboard");
           $(".shift-space-row").show();
           $(".type-area").show();
           $(".long").show();
           $(".keypress:not(.shift-btn)").addClass('btn-default');
           $(".keypress:not(.shift-btn)").removeClass('btn-info');
           $(".keypress:not(.shift-btn)").click(btnClick);
       }
        return false;
    });



};

jQuery(document).ready(ready);
jQuery(document).on('page:load', ready);