/**
 * Created by simeon on 10/10/14.
 */


$(function() {
    var elementText = $(".js-example-responsive").attr('data-initvalue');
    var arr = [];
    if(elementText != undefined){
        var keyboard = JSON.parse(elementText);
        for(var i = 0, len = keyboard .length; i < len; i++) {
            arr.push( {"id" : keyboard[i].id,"text": keyboard[i].name});
        }
    }

    //console.log(arr);
    $('.edit').editable('/../update_keyboard_name', {
        data    : function(string) {return string.replace('<button class="glyphicon glyphicon-edit "></button>','');},
        type      : 'text',
        cancel    : ' <button class="glyphicon glyphicon-remove editbtn btnsuccess" ></button>',
        submit    : ' <button class="glyphicon glyphicon-ok editbtn " ></button>',
        style      : "display: inline",
        method    : 'post',
        tooltip   : 'Click to edit...',
        callback: function(value) {
            $(this).html(value+'<button class="glyphicon glyphicon-edit "></button>');
        }
    });

    $(".js-example-responsive").select2({
        placeholder: "Select a keyboard",
        data: arr
    });

    $('#auto-lang').autocomplete({
        minLength: 2,
        source: $('#auto-lang').data('autocomplete-source'),
        response: function(event, ui) {
            // ui.content is the array that's about to be sent to the response callback.
            if (ui.content.length === 0) {
                $('#auto-lang').val('');
                $('#auto-lang').focus();
                $('#auto-lang').attr("placeholder", "Not a valid language");
            }
        },
        select: function(event, ui) {
            $('#auto-lang').removeAttr('style');
            var language = ui.item.value;
            var code = language.split('-');
            $('#keyboard_iso_language').val(code[0]);
            $.ajax({
                type: "POST",
                url: "/get_reg_name",
                data: {lang: code[0]},
                success: function(response) {
                    //console.log(response.region_name);
                    if(response.region_name != 'no_region'){
                        $('#auto-region').val(response.region_name);
                        var reg = response.region_name.split('-');
                        $('#keyboard_iso_region').val(reg[0]);
                    }else {
                        $('#auto-region').val('No Region');
                    }
                }
            });
        }
    })

});

readyNewKeyboard = function() {

    $("#keyboard_name").keyup(function(){
        $('#keyboard_name').parent().removeClass("has-error")
        $('#keyboard_name').removeAttr('style');
        $('#keyboard_name').css('border-color','')
    });
    $("#auto-lang").change(function(){
        $('#auto-lang').css('border-color','')
        $('#auto-lang').removeAttr('style');
        $('#auto-lang').parent().removeClass("has-error")
    });
    $(".new-keyboard-save").click(function(){
        var lang = $('#auto-lang').val();
        var keyboard_name = $('#keyboard_name').val();
        if(keyboard_name.trim() == "") {
            $('#keyboard_name').val('');
            $('#keyboard_name').focus();
            $('#keyboard_name').attr("required", "true");
            $('#keyboard_name').parent().addClass("has-error")
            $('#keyboard_name').css('border-color','red')
            $('#keyboard_name').attr("placeholder", "Enter valid name for keyboard");
            return false;
        }
        if(lang.trim() == "") {
            $('#keyboard_name').parent().removeClass("has-error")
            $('#auto-lang').val('');
            $('#auto-lang').focus();
            $('#auto-lang').attr("required", "true");
            $('#auto-lang').css('border-color','red')
            $('#auto-lang').parent().addClass("has-error")
            $('#auto-lang').attr("placeholder", "Enter valid language");
            return false;
        }
        $('#keyboard_name').css('border-color','')
        $('#auto-lang').css('border-color','')
        $('#keyboard_name').parent().removeClass("has-error")
        $('#auto-lang').parent().removeClass("has-error")
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
