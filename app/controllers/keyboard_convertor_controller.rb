class KeyboardConvertorController < ApplicationController
  def convert
  	rows = Nokogiri::XML(File.open(kbd_qwerty))

  	rows.css('Row').each do |row|
  		


  	keys.each do |key|
  		character = Character.new;

  end
end
