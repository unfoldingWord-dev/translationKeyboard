# Place all the behaviors and hooks related to the matching controller here.
# All this logic will automatically be available in application.js.
# You can use CoffeeScript in this file: http://coffeescript.org/

$(document).ready ->
  $(".key_position_form").on("ajax:success", (e, data, status, xhr) ->
    location.reload()
    $(".key_position_form").append xhr.responseText ->

  ).on "ajax:error", (e, xhr, status, error) ->
    $(".key_position_form").append "<p>ERROR</p>"

$(document).ready ->
    $(".new_keyboard_form").on("ajax:success", (e, data, status, xhr) ->
      objData = $.parseJSON( data )
      window.location.href = "/keyboard/variant/" + objData[0].id
      $(".new_keyboard_form").append xhr.responseText ->

    ).on "ajax:error", (e, xhr, status, error) ->
    $(".new_keyboard_form").append "<p>ERROR</p>"