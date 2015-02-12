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
//= require jquery-ui
//= require autocomplete-rails


$('#keyboard_lang').autocomplete("option", "appendTo", ".modal-dialog");

function update_region_name(keyboard_id,btn_id) 
{
	var index = $(btn_id).attr('id');
	var reg_name = $('#iso_region_'+index).val();
	$.ajax({
		    type: "POST", 
		    url: "/save_region_name",
		    data: {id: keyboard_id,region:reg_name},
		    success: function(response) {
			$('#accordion').empty();
			$('#accordion').append(response);
		    }
	});
}

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
            trigger: 'click',
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
	
    // For movement of keys
    $( ".button-div" ).sortable({
	       	cancel: false,
		start: function(event, ui) {
			$(this).addClass('noclick');
			//console.log('start: ' + ui.item.index())
			start_order =  ui.item.index();
			$('.samplediv').removeClass('ui-sortable-handle');
		},
		stop: function(event, ui) {
				$('.samplediv').removeClass('ui-sortable-handle');
				var number_of_keys = 0;
				var row_index = 0;
				var divId = $(this).attr('id');
				//console.log(divId);
				var div_array = "";
				div_array = divId.split("_");
				no_of_keys = div_array[1];
				row_index = div_array[2];
				class_name = div_array[3];
				var n = 0;
				var stringDiv = "";
				order = ui.item.index();
				$(this).children().each(function(i) {
				    var li = $(this);
				    div_prev_id = li.attr("id");
					if(div_prev_id.indexOf('sample_div') < 0){
	   				    new_id = div_prev_id.split('-');
					    div_class_name = li.attr('class');
					    if(div_class_name.indexOf('col-xs-offset') >= 0) {
						li.removeClass(class_name);
					    }
					    stringDiv += new_id[1] + '=' + i + '&';
					    if(i == 0) {
						li.addClass(class_name);
					    }
					}	
				});
			//console.log(stringDiv);
	    		$.ajax({
			    type: "POST", 
			    url: "/save_new_position",
			    data: {order: stringDiv},
			    success: function(response) {
				
			    }
			});
		}
	    });
    $( ".button-div" ).sortable().disableSelection();
   //$(".button-div").sortable("option", "cancel", ':input,button,a');
    $(document).on('click','.modbtn',function(e){
	if($(this).hasClass('btn-info')) {
	    if ($(this).parent().hasClass('noclick')) {
		$(this).parent().removeClass('noclick');
	    }else {
		$('input[type=text]').bind('mousedown.ui-disableSelection selectstart.ui-disableSelection', function(event) {
		      event.stopImmediatePropagation();
		})
		$('#key_edit_modal_'+$(this).data('id')).modal('show');
	    }
	}
    });
	
    $(".edit-keyboard").click(function(event){
        event.preventDefault();
        $(".keypress:not(.shift-btn)").unbind("click");
        if($(this).hasClass("btn-primary")){
           $(this).removeClass("btn-primary");
           $(this).addClass("btn-success");
           $(this).text("Done");
	   $('.samplediv').removeClass('ui-sortable-handle');
	   $(".addition").show();
	   $(".remove").show();
	   $(".shift-space-row").hide();
           $(".type-area").hide();
           $(".long").hide();
	   $('.button-div').addClass('whenEdit');
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
	   $(".addition").hide();
	    $(".remove").hide();
           $(".keypress:not(.shift-btn)").addClass('btn-default');
           $(".keypress:not(.shift-btn)").removeClass('btn-info');
           $(".keypress:not(.shift-btn)").click(btnClick);
       }
        return false;
    });

	

};


jQuery(document).ready(ready);
jQuery(document).on('page:load', ready);
