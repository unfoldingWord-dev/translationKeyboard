# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20141007202431) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "characters", force: true do |t|
    t.integer  "modmask"
    t.integer  "sortnumber"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "key_position_id"
    t.integer  "unicode_character_id"
  end

  create_table "key_positions", force: true do |t|
    t.integer  "column_index"
    t.integer  "row_index"
    t.float    "percent_width"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "keyboard_variant_id"
  end

  create_table "keyboard_types", force: true do |t|
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "keyboard_variants", force: true do |t|
    t.string   "name"
    t.integer  "keyboard_type_id"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "keyboard_id"
  end

  create_table "keyboards", force: true do |t|
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "iso_region"
    t.string   "iso_language"
  end

  create_table "unicode_characters", force: true do |t|
    t.string   "englishDesc"
    t.integer  "utf8hex"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  create_table "users", force: true do |t|
    t.string   "email",                  default: "", null: false
    t.string   "encrypted_password",     default: "", null: false
    t.string   "reset_password_token"
    t.datetime "reset_password_sent_at"
    t.datetime "remember_created_at"
    t.integer  "sign_in_count",          default: 0,  null: false
    t.datetime "current_sign_in_at"
    t.datetime "last_sign_in_at"
    t.inet     "current_sign_in_ip"
    t.inet     "last_sign_in_ip"
    t.datetime "created_at"
    t.datetime "updated_at"
  end

  add_index "users", ["email"], name: "index_users_on_email", unique: true, using: :btree
  add_index "users", ["reset_password_token"], name: "index_users_on_reset_password_token", unique: true, using: :btree

end
