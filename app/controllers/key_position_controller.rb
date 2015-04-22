class KeyPositionController < ApplicationController

  def create

  end

  def update

   # redirect_to "/keyboard/variant/#{params[:id]}"
    @a_key_position = KeyPosition.find(params[:id])
    @characters = @a_key_position.characters
    @post_characters = params[:key_position][:character]
    @array_of_character_ids = []
    params[:key_position][:character].each do |k, v|
      if !k.include? 'new'
        character_to_update = @characters.find(k)
        character_unicode = character_to_update.character_unicode_mapping
        if character_unicode
          character_unicode.each{|char_unicode| char_unicode.destroy}
        end
      end
      if v['utf8hex'].include? ';'

        index = 1
        hex_values = v['utf8hex'].split(';')
        if k.include? 'new'
          new_char = Character.create([{key_position_id: @a_key_position.id, modmask: 2, sortnumber:1, unicode_character_id: 96 }])
        end
        hex_values.each do |hex_value|
          utf8Value = hex_value.hex
          associated_unicode_record = get_or_create_unicode_record utf8Value
          if k.include? 'new'
            save_hex_char k, new_char.first.id, associated_unicode_record.id, index
          else
            save_hex_char k, @a_key_position.id, associated_unicode_record.id, index
          end
          index += 1
        end
      else
        utf8Value = v['utf8hex'].hex
        associated_unicode_record = get_or_create_unicode_record utf8Value
        if k.include? 'new'
          new_char = Character.create([{key_position_id: @a_key_position.id, modmask: 2, sortnumber:1, unicode_character_id: associated_unicode_record.id }])
        end
        save_hex_char k, @a_key_position.id, associated_unicode_record.id, index
      end
    end

    # if character has been removed delete it from the database
    @characters.each do |character|
      unless @array_of_character_ids.include? character.id
        character.destroy
      end
    end

    respond_to do |format|
      format.html #update.html.erb
      format.json { render json: @a_key_position, status: :updated, location: @a_key_position}
      format.js {}
    end
  end

  def save_hex_char (k, key_pos_id, unicode_id, order)
    if k.include? 'new'
      CharacterUnicodeMapping.create([{character_id: key_pos_id, unicode_character_id: unicode_id, order: order}])
      @array_of_character_ids.push(key_pos_id.to_i)
    else
      @array_of_character_ids.push(k.to_i)
      character_to_update = @characters.find(k)
      character_to_update.unicode_character_id = unicode_id
      character_to_update.save
      CharacterUnicodeMapping.create([{character_id: character_to_update.id, unicode_character_id: unicode_id, order: order}])
      @a_key_position.keyboard_variant.touch
      @a_key_position.keyboard_variant.keyboard.touch
    end

  end

  private_methods

  def get_or_create_unicode_record utf8Value
    associated_unicode_record = UnicodeCharacter.where(utf8hex: utf8Value).first
    if associated_unicode_record.nil?
      associated_unicode_record = UnicodeCharacter.create({englishDesc:"", utf8hex: utf8Value})
    end
    associated_unicode_record
  end


end
