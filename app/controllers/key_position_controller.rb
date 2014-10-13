class KeyPositionController < ApplicationController

  def create

  end

  def update
    @a_key_position = KeyPosition.find(params[:id])
    @characters = @a_key_position.characters

    @post_characters = params[:key_position][:character]

    params[:key_position][:character].each do |k, v|
      character_to_update = @characters.find(k)
      utf8Value = v['utf8hex'].hex
      associated_unicode_record = UnicodeCharacter.where(utf8hex: utf8Value).first
      if associated_unicode_record.nil?
        associated_unicode_record = UnicodeCharacter.create({englishDesc:"", utf8hex: utf8Value})
      end
      character_to_update.unicode_character_id = associated_unicode_record.id
      character_to_update.save
    end

    respond_to do |format|
      format.html #update.html.erb
      format.json { render json: @a_key_position, status: :updated, location: @a_key_position}
      format.js {}
    end


  end

end
