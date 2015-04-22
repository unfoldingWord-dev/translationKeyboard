class KeyboardController < ApplicationController
  autocomplete :keyboard_languages, :lc, :full=> true, :extra_data => [:ln], :display_value => :funky_method
  before_action :authenticate_user!
  def index
    get_all_keyboards
    @keyboard = Keyboard.first
    @selected_keyboard_variant = @keyboard.keyboard_variants.first
    @parent_keyboard = @selected_keyboard_variant.keyboard
    set_key_positions
    get_new_key_position
    redirect_to "/keyboard/variant/#{@selected_keyboard_variant.id}"
  end

  def create


      if Keyboard.where(iso_language: keyboard_params[:iso_language],iso_region: keyboard_params[:iso_region]).count < 1
        new_keyboard = Keyboard.create(keyboard_params)
      else
        new_keyboard = Keyboard.find_by(iso_language: keyboard_params[:iso_language],iso_region: keyboard_params[:iso_region])
      end
       if params[:load_another_keyboard].blank?
         the_keyboard_type = KeyboardType.find(params[:keyboard_type_id])
       else
        the_keyboard_variant =  Keyboard.find(params[:load_another_keyboard]).keyboard_variants
        the_keyboard_type = KeyboardType.find(the_keyboard_variant.first.keyboard_type_id)
       end
      new_keyboard_variant = KeyboardVariant.create([{keyboard: new_keyboard, keyboard_type: the_keyboard_type,
                                                     name: new_keyboard.name + ' ' + the_keyboard_type.name }])
      if params[:load_another_keyboard].blank?

        the_keyboard_type.keyboard_type_default_key_positions.each do |default_key_pos|
          increment = 0
          until increment >= default_key_pos.col_count do
            new_key_pos = KeyPosition.create([{row_index: default_key_pos.row_index, column_index:increment, percent_width:1, keyboard_variant: new_keyboard_variant.first}])
            unicode_char = UnicodeCharacter.find_or_create_by(utf8hex:'20'.hex)
            new_characters = Character.create([{modmask: '0'.to_i(2), sortnumber: 1, unicode_character:unicode_char, key_position:new_key_pos.first},
                                               {modmask: '1'.to_i(2), sortnumber: 1, unicode_character:unicode_char, key_position:new_key_pos.first}])
            increment += 1
          end

        end
      else

        @existing_key_positions = KeyPosition.where(:keyboard_variant_id => the_keyboard_variant.first.id)
        @existing_key_positions.each do |key_pos|
          new_key_pos = KeyPosition.create([{row_index: key_pos.row_index, column_index: key_pos.column_index, percent_width:1, keyboard_variant: new_keyboard_variant.first}])
          #@keyboard_characters = Character.where(:key_position_id => key_pos.id)
          @keyboard_characters = key_pos.characters
          @keyboard_characters.each do |key_char|
            Character.create([{modmask: key_char.modmask, sortnumber: 1, unicode_character_id: key_char.unicode_character_id, key_position:new_key_pos.first}])
          end
        end

      end

      #render plain: @existing_key_positions.first.characters.last.unicode_character_id
      respond_to do |format|
        format.html #update.html.erb
        format.json { render json: new_keyboard, status: :created, location: new_keyboard_variant}
        format.js {render :json => new_keyboard_variant}
      end

   end



  def edit
  
  end

  def update_keyboard_name
    keyboard_id = params[:id]
    name = params[:value]
    @result = Keyboard.update(keyboard_id, :name => name)
    @keyboard_variants = Keyboard.find(@result.id).keyboard_variants
    @keyboard_variants.each do |variant|
      variant_name = variant.keyboard_type.name
      variant.update(:name => name )
    end
    render plain: name
  end
  def variant
    get_all_keyboards
    @selected_keyboard_variant = KeyboardVariant.find(params[:keyboard_variant_id])
    @parent_keyboard = @selected_keyboard_variant.keyboard
    @parent_keyboard_list = Keyboard.where(id: @parent_keyboard.id)
    set_key_positions

    render 'keyboard/index'
  end

  def variant_destroy
    @selected_keyboard_variant = KeyboardVariant.find(params[:keyboard_variant_id])
    variant_count = @selected_keyboard_variant.keyboard.keyboard_variants.count
    the_root_keyboard = @selected_keyboard_variant.keyboard
    @selected_keyboard_variant.delete

    if variant_count <= 1
      keyboard_to_destroy = Keyboard.find(the_root_keyboard.id)
      keyboard_to_destroy.delete
    end
    redirect_to root_url
  end

  def add_row_key
    row_index = params["row_no"]
    keyboard_variant = KeyboardVariant.find(params["keyboard_variant"])
    @position = KeyPosition.where(keyboard_variant: keyboard_variant, row_index: row_index).last
    @position_details = KeyPosition.find(@position)
    column_index = @position_details.column_index + 1
    new_key_pos = KeyPosition.create([{row_index: row_index, column_index:column_index, percent_width:1, keyboard_variant: keyboard_variant}])
    unicode_char = UnicodeCharacter.find_or_create_by(utf8hex:'20'.hex)
    new_characters = Character.create([{modmask: '0'.to_i(2), sortnumber: 1, unicode_character:unicode_char, key_position:new_key_pos.first},
                                      {modmask: '1'.to_i(2), sortnumber: 1, unicode_character:unicode_char, key_position:new_key_pos.first}])
    #result = keyboard_variant.id
   # @key_positions = KeyPosition.where(keyboard_variant: keyboard_variant)
    @key_positions = KeyPosition.where(keyboard_variant: keyboard_variant).order(:row_index, column_index: :asc)
   render :partial => 'modals/key_update', :locals => {:key_positions => @key_positions}

  end

  def remove_row_key
    row_no = params["row_no"]
    keyboard_variant = KeyboardVariant.find(params["keyboard_variant"])
    @position = KeyPosition.where(keyboard_variant: keyboard_variant, row_index: row_no).last
    #response = KeyPosition.find(@position)
    @characters = Character.where(key_position_id: @position)

    @characters.each do |one_character|
      one_character.destroy
    end
    @position.destroy
    @key_positions = KeyPosition.where(keyboard_variant: keyboard_variant).order(:row_index, column_index: :asc)
    render :partial => 'modals/key_update', :locals => {:key_positions => @key_positions}

  end

  def key_edit
	item_id = params["id"]
	@key_position = KeyPosition.find(item_id)
	render :partial => 'modals/key_edit', :locals => {:key_position => @key_position}
	
  end

  def load_char
    key_id = params['id']
    key_position = KeyPosition.find(key_id)
    default = ''
    shift = ''
    long = ''
    span = ''
    longPress = []
    if key_position.characters.where(modmask: 0).exists?
      default = (key_position.characters.where(modmask: 0).first.unicode_character.utf8hex).chr
      span = (key_position.characters.where(modmask: '0'.to_i(2)).first.unicode_character.utf8hex).chr
    end
    if key_position.characters.where('modmask & ? > 0', '1'.to_i(2)).exists?
      shift = (key_position.characters.where('modmask & ? > 0', '1'.to_i(2)).first.unicode_character.utf8hex).chr
    end
    if key_position.characters.where('modmask & ? > 0', "10".to_i(2)).exists?
      @longPressCharacters = key_position.characters.where("modmask & ? > 0", "10".to_i(2)).order(sortnumber: :asc)
      limit = 0
      @longPressCharacters.each do |longPressCharacter|
        longPress[limit] = (longPressCharacter.unicode_character.utf8hex).chr
        limit = limit + 1
      end
    end
    response = {:default => default, :span => span, :shift => shift, :long => longPress}
    respond_to do |format|
      format.json {render :json => response}
    end
  end

  def import_lang_region
    file_content = File.read('langnames.json')
    languages = JSON.parse(file_content)
    #KeyboardCountry.destroy_all
    #KeyboardLanguages.destroy_all
    #LangRegions.destroy_all
    languages.each do |item|
     if item['cc'][0] != ''
       new_country = KeyboardCountry.find_or_create_by(cc: item['cc'][0])
     end
     new_languages = KeyboardLanguages.find_or_create_by(ln: item['ln'], lc: item['lc'], lr: item['lr'])
     if item['cc'][0] != ''
       LangRegions.find_or_create_by(keyboardCountry_id: new_country.id,  keyboardLanguages_id: new_languages.id)
     end
    end
    render :action => 'import_lang'
  end


  def update_position
	  order = params["order"].split('&')
	  order.each do |d|
	    if d != ''
		    split_with_id_index = d.split('=')
   		  id = split_with_id_index[0]
		    index = split_with_id_index[1]
		    @key_details = KeyPosition.find(id)
        @key_details.update_attributes(:column_index => index)
	    end	
	  end
    response = true
    respond_to do |format|
      format.json {render :json => response}
    end
  end

  def load_all_keyboard
    @keyboards = Keyboard.select('id, name')
    respond_to do |format|
      format.html
      format.json { render json: @keyboards.where('name like ?', "%#{params[:term]}%") }
    end
  end

  private
  def get_all_keyboards
    @all_keyboards = Keyboard.all.group("iso_language, id").order(:iso_language)
  end

  def set_key_positions
    @key_positions = KeyPosition.where(keyboard_variant: @selected_keyboard_variant).order(:row_index, column_index: :asc)
  end

  def get_new_key_position
    @new_key_position = KeyPosition.where(keyboard_variant: @selected_keyboard_variant).order(:row_index, column_index: :asc).first
  end

  def keyboard_params
    params.require(:keyboard).permit(:name, :iso_language, :iso_region)
  end

end
