/**
 * Created by simeon on 10/10/14.
 */

readyNewKeyboard = function() {


    $(".new-keyboard-save").click(function(){
        $(this).addClass("disabled");
        $("#new_keyboard").submit();
    });


};


function getlang() {
	var lang_with_code = document.getElementById('keyboard_iso_language').value;
	var code = lang_with_code.split('-');
	document.getElementById('keyboard_iso_language').value = code[0];
	return true;
}
function addkey(rowNo,variant) {
       $.ajax({
		    type: "POST", 
		    url: "/add_key",
		    data: {row_no: rowNo,
             keyboard_variant: variant},
		    success: function(response) {
				$('#keyboard_id').empty();
				$('#keyboard_id').append(response);	
				 readyEditKey();
				$(document).ready(function() {
					$(".key_position_form").on("ajax:success", function(e, data, status, xhr) {
					      location.reload();
					}).on("ajax:error", function(e, xhr, status, error) {
					      $(".key_position_form").append("<p>ERROR</p>");
					});
				});
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
			    $( ".button-div" ).disableSelection();
			    $(document).on('click','.modbtn',function(e){
				if($(this).hasClass('btn-info')) {
				    if ($(this).parent().hasClass('noclick')) {
					$(this).parent().removeClass('noclick');
				    }else {
		
					$('#key_edit_modal_'+$(this).data('id')).modal('show');
					$('input[type=text]').bind('mousedown.ui-disableSelection selectstart.ui-disableSelection', function(event) {
						      event.stopImmediatePropagation();
						})
				    }
				}
			    });
			
		}	
	});
    }
    function removekey(rowNo,variant) {
	$.ajax({
		    type: "POST", 
		    url: "/remove_key",
		    data: {row_no: rowNo,keyboard_variant:variant},
		    success: function(response) {
		    	$('#keyboard_id').empty();
			$('#keyboard_id').append(response);		
			 readyEditKey();
				$(document).ready(function() {
					$(".key_position_form").on("ajax:success", function(e, data, status, xhr) {
					      location.reload();
					}).on("ajax:error", function(e, xhr, status, error) {
					      $(".key_position_form").append("<p>ERROR</p>");
					});
				});
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
			    $( ".button-div" ).disableSelection();
			    $(document).on('click','.modbtn',function(e){
				if($(this).hasClass('btn-info')) {
				    if ($(this).parent().hasClass('noclick')) {
					$(this).parent().removeClass('noclick');
				    }else {
		
					$('#key_edit_modal_'+$(this).data('id')).modal('show');
					$('input[type=text]').bind('mousedown.ui-disableSelection selectstart.ui-disableSelection', function(event) {
					      event.stopImmediatePropagation();
					})
				    }
				}
			    });					
		    }
	});
    }

jQuery(document).ready(readyNewKeyboard);
jQuery(document).on('page:load', readyNewKeyboard);
