class KeyboardController < ApplicationController

  before_action :authenticate_user!
  def index
    get_all_keyboards
    @keyboard = Keyboard.first
    @selected_keyboard_variant = @keyboard.keyboard_variants.first
    @parent_keyboard = @selected_keyboard_variant.keyboard
    set_key_positions
    get_new_key_position
    redirect_to "/keyboard/get_keyboard_variant/#{@selected_keyboard_variant.id}"
  end

  def create
    new_keyboard = Keyboard.create(keyboard_params)
    the_keyboard_type = KeyboardType.find(params[:keyboard_type_id])
    new_keyboard_variant = KeyboardVariant.create([{keyboard: new_keyboard, keyboard_type: the_keyboard_type,
                                                    name: new_keyboard.name << ' ' << the_keyboard_type.name }])

    the_keyboard_type.keyboard_type_default_key_positions.each do |default_key_pos|
      increment = 0
      until increment >= default_key_pos.col_count do
        new_key_pos = KeyPosition.create([{row_index: default_key_pos.row_index, column_index:increment, percent_width:1, keyboard_variant: new_keyboard_variant.first}])
        unicode_char = UnicodeCharacter.find_or_create_by(utf8hex:'30'.hex)
        new_characters = Character.create([{modmask: '0'.to_i(2), sortnumber: 1, unicode_character:unicode_char, key_position:new_key_pos.first},
                                           {modmask: '1'.to_i(2), sortnumber: 1, unicode_character:unicode_char, key_position:new_key_pos.first}])
        increment += 1
      end

    end

    respond_to do |format|
      format.html #update.html.erb
      format.json { render json: new_keyboard, status: :created, location: new_keyboard_variant}
      format.js {render :json => new_keyboard_variant}
    end

   end

  def edit
  
  end
  def get_keyboard_variant
    get_all_keyboards
    @selected_keyboard_variant = KeyboardVariant.find(params[:keyboard_variant_id])
    @parent_keyboard = @selected_keyboard_variant.keyboard
    set_key_positions

    render "keyboard/index"
  end


  private
  def get_all_keyboards
    @all_keyboards = Keyboard.all
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
