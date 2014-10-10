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

end
